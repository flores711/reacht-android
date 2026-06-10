package com.example.reacht_android

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reacht_android.model.Chat
import com.example.reacht_android.model.ChatMessage
import com.example.reacht_android.model.Offer
import com.example.reacht_android.model.Videogame
import com.example.reacht_android.network.SocketClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.net.SocketTimeoutException

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
    object LoginSuccess : AuthState()
    object SignupSuccess : AuthState()
}

sealed class JoinOfferState {
    object Idle : JoinOfferState()
    data class Success(val chatId: Int, val chatName: String) : JoinOfferState()
    data class Error(val message: String) : JoinOfferState()
}

sealed class UpdateUserState {
    object Idle : UpdateUserState()
    object Success : UpdateUserState()
    data class Error(val message: String) : UpdateUserState()
}

sealed class CreateOfferState {
    object Idle : CreateOfferState()
    data class Success(val chatId: Int, val chatName: String) : CreateOfferState()
    data class Error(val message: String) : CreateOfferState()
}

class AppViewModel : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            SocketClient.connect()
        }
    }

    var userId: Int = -1
    var username: String = ""

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    private val _offers = MutableStateFlow<List<Offer>>(emptyList())
    val offers = _offers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _selectedOffer = MutableStateFlow<Offer?>(null)
    val selectedOffer = _selectedOffer.asStateFlow()

    private val _currentOffer = MutableStateFlow<Offer?>(null)
    val currentOffer = _currentOffer.asStateFlow()

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats = _chats.asStateFlow()

    private val _joinOfferState = MutableStateFlow<JoinOfferState>(JoinOfferState.Idle)
    val joinOfferState = _joinOfferState.asStateFlow()

    var selectedChat: Chat? = null
    private var chatListenerJob: Job? = null

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages = _chatMessages.asStateFlow()

    private val _userEmail = MutableStateFlow("")
    val userEmail = _userEmail.asStateFlow()

    private val _updateUserState = MutableStateFlow<UpdateUserState>(UpdateUserState.Idle)
    val updateUserState = _updateUserState.asStateFlow()

    private val _videogames = MutableStateFlow<List<Videogame>>(emptyList())
    val videogames = _videogames.asStateFlow()

    private val _createOfferState = MutableStateFlow<CreateOfferState>(CreateOfferState.Idle)
    val createOfferState = _createOfferState.asStateFlow()

    private val _leaveChatSuccess = MutableStateFlow(false)
    val leaveChatSuccess = _leaveChatSuccess.asStateFlow()

    fun selectOffer(offer: Offer) {
        _selectedOffer.value = offer
    }

    fun selectChat(chat: Chat) {
        selectedChat = chat
    }

    fun resetJoinOfferState() {
        _joinOfferState.value = JoinOfferState.Idle
    }

    fun joinOffer(offerId: Int) {
        // Cualquier función de red debe estar en un hilo aparte
        // Si no se bloquea/congela la aplicación hasta que llegue respuesta, propio jetpack compose lo prohíbe
        // Y como todas estas funciones llaman a send() del SocketClient, que se comunica por red
        // y debe usar un hilo aparte, estos también deben hacerlo
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = JSONObject().apply {
                    put("action", "JOIN_OFFER")
                    put("data", JSONObject().apply {
                        put("offer_id", offerId)
                        put("user_id", userId)
                    })
                }
                val responseStr = SocketClient.send(request.toString())
                val response = JSONObject(responseStr)
                when (response.getString("action")) {
                    "JOIN_OFFER_SUCCESS" -> {
                        val data = response.getJSONObject("data")
                        val chatId = data.getInt("chat_id")
                        val chatName = data.getString("chat_name")
                        _joinOfferState.value = JoinOfferState.Success(chatId, chatName)
                    }
                    else -> {
                        val message = response.getJSONObject("data").optString("message", "Failed to join offer")
                        _joinOfferState.value = JoinOfferState.Error(message)
                    }
                }
            } catch (e: Exception) {
                _joinOfferState.value = JoinOfferState.Error("Connection error: ${e.message}")
            }
        }
    }

    fun leaveOffer(offerId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = JSONObject().apply {
                    put("action", "LEAVE_OFFER")
                    put("data", JSONObject().apply {
                        put("offer_id", offerId)
                        put("user_id", userId)
                    })
                }
                val responseStr = SocketClient.send(request.toString())
                val response = JSONObject(responseStr)
                if (response.getString("action") == "LEAVE_OFFER_SUCCESS") {
                    _currentOffer.value = null
                }
            } catch (e: Exception) {
                // silent fail — user can retry
            }
        }
    }

    fun loadCurrentOffer() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val request = JSONObject().apply {
                    put("action", "GET_CURRENT_OFFER")
                    put("data", JSONObject().apply {
                        put("user_id", userId)
                    })
                }
                val responseStr = SocketClient.send(request.toString())
                val response = JSONObject(responseStr)
                when (response.getString("action")) {
                    "GET_CURRENT_OFFER_SUCCESS" -> {
                        val d = response.getJSONObject("data")
                        _currentOffer.value = Offer(
                            offerId = d.getInt("offer_id"),
                            description = d.getString("description"),
                            currentPlayers = d.getInt("current_players"),
                            targetPlayers = d.getInt("target_players"),
                            videogame = Videogame(d.getInt("videogame_id"), d.getString("videogame_title")),
                            creatorId = d.getInt("creator_id"),
                            creatorUsername = d.getString("creator_username")
                        )
                    }
                    "GET_CURRENT_OFFER_NOT_FOUND" -> _currentOffer.value = null
                }
            } catch (e: Exception) {
                // _currentOffer stays unchanged
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadChats() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val request = JSONObject().apply {
                    put("action", "GET_USER_CHATS")
                    put("data", JSONObject().apply {
                        put("user_id", userId)
                    })
                }
                val responseStr = SocketClient.send(request.toString())
                val response = JSONObject(responseStr)
                if (response.getString("action") == "GET_USER_CHATS_SUCCESS") {
                    val chatsArray = response.getJSONObject("data").getJSONArray("chats")
                    val chatList = mutableListOf<Chat>()
                    for (i in 0 until chatsArray.length()) {
                        val c = chatsArray.getJSONObject(i)
                        chatList.add(Chat(chatId = c.getInt("chat_id"), name = c.getString("name")))
                    }
                    _chats.value = chatList
                }
            } catch (e: Exception) {
                // _chats stays unchanged
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun enterChat(chatId: Int) {
        _chatMessages.value = emptyList()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = JSONObject().apply {
                    put("action", "GET_CHAT_HISTORY")
                    put("data", JSONObject().apply {
                        put("chat_id", chatId)
                    })
                }
                val responseStr = SocketClient.send(request.toString())
                val response = JSONObject(responseStr)
                if (response.getString("action") == "GET_CHAT_HISTORY_SUCCESS") {
                    val messagesArray = response.getJSONObject("data").getJSONArray("messages")
                    val list = mutableListOf<ChatMessage>()
                    for (i in 0 until messagesArray.length()) {
                        val m = messagesArray.getJSONObject(i)
                        list.add(ChatMessage(
                            messageId = m.getInt("message_id"),
                            userId = m.getInt("user_id"),
                            chatId = m.getInt("chat_id"),
                            timestamp = m.getString("timestamp"),
                            text = m.getString("text"),
                            userUsername = m.getString("user_username")
                        ))
                    }
                    // Actualizamos variable del viewmodel con la lista ya llena
                    _chatMessages.value = list
                }
            } catch (e: Exception) {
                // _chatMessages stays empty
            }
        }

        // Lanzamos hilo de escucha para la lista de mensajes nuevos del socketclient
        // Para añadirlos al chat conforme vayan llegando cuando estemos dentro
        // Lo guardamos en variable para luego poder terminar el hilo cuando salgamos del chat
        chatListenerJob = viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                // take() espera hasta que haya algo para cogerlo
                val json = SocketClient.newChatMessageQueue.take()
                try {
                    val data = JSONObject(json).getJSONObject("data")
                    val newMessage = ChatMessage(
                        messageId = data.getInt("message_id"),
                        userId = data.getInt("user_id"),
                        chatId = data.getInt("chat_id"),
                        timestamp = data.getString("timestamp"),
                        text = data.getString("text"),
                        userUsername = data.getString("user_username")
                    )
                    _chatMessages.value += newMessage
                } catch (e: Exception) {
                    // mensaje malformado, ignorar
                }
            }
        }
    }

    fun sendMessage(chatId: Int, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = JSONObject().apply {
                    put("action", "SEND_MESSAGE")
                    put("data", JSONObject().apply {
                        put("user", JSONObject().apply { put("id", userId) })
                        put("chat", JSONObject().apply { put("id", chatId) })
                        put("text", text)
                    })
                }
                SocketClient.send(request.toString())
            } catch (e: Exception) {
                // silent fail
            }
        }
    }

    fun exitChat() {
        // Paramos el hilo y reseteamos la variable
        chatListenerJob?.cancel()
        chatListenerJob = null
        // Vaciamaos la lista para usarla limpia para el siguiente chat
        _chatMessages.value = emptyList()
    }

    fun leaveChat(chatId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = JSONObject().apply {
                    put("action", "LEAVE_CHAT")
                    put("data", JSONObject().apply {
                        put("chat_id", chatId)
                        put("user_id", userId)
                    })
                }
                val responseStr = SocketClient.send(request.toString())
                val response = JSONObject(responseStr)
                if (response.getString("action") == "LEAVE_CHAT_SUCCESS") {
                    _chats.value = _chats.value.filter { it.chatId != chatId }
                    exitChat()
                    _leaveChatSuccess.value = true
                }
            } catch (e: Exception) {
                // silent fail
            }
        }
    }

    fun resetLeaveChatSuccess() {
        _leaveChatSuccess.value = false
    }

    fun loadUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = JSONObject().apply {
                    put("action", "GET_USER_DATA")
                    put("data", JSONObject().apply {
                        put("user_id", userId)
                    })
                }
                val responseStr = SocketClient.send(request.toString())
                val response = JSONObject(responseStr)
                if (response.getString("action") == "GET_USER_DATA_SUCCESS") {
                    _userEmail.value = response.getJSONObject("data").getString("email")
                }
            } catch (e: Exception) {
                // _userEmail stays unchanged
            }
        }
    }

    fun updateUser(newUsername: String, newEmail: String, newPassword: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = JSONObject().apply {
                    put("action", "UPDATE_USER")
                    put("data", JSONObject().apply {
                        put("user_id", userId)
                        put("username", newUsername)
                        put("email", newEmail)
                        if (newPassword != null) put("password", newPassword)
                    })
                }
                val responseStr = SocketClient.send(request.toString())
                val response = JSONObject(responseStr)
                if (response.getString("action") == "UPDATE_USER_SUCCESS") {
                    username = newUsername
                    _updateUserState.value = UpdateUserState.Success
                } else {
                    // TODO: Por qué opt string y la respuesta por defecto aquí
                    val message = response.getJSONObject("data").optString("message", "Update failed")
                    _updateUserState.value = UpdateUserState.Error(message)
                }
            } catch (e: Exception) {
                _updateUserState.value = UpdateUserState.Error("Connection error: ${e.message}")
            }
        }
    }

    fun resetUpdateUserState() {
        _updateUserState.value = UpdateUserState.Idle
    }

    fun loadVideogames() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = JSONObject().apply {
                    put("action", "GET_VIDEOGAMES")
                }
                val responseStr = SocketClient.send(request.toString())
                val response = JSONObject(responseStr)
                if (response.getString("action") == "GET_VIDEOGAMES_SUCCESS") {
                    val array = response.getJSONObject("data").getJSONArray("videogames")
                    val list = mutableListOf<Videogame>()
                    for (i in 0 until array.length()) {
                        val v = array.getJSONObject(i)
                        list.add(Videogame(
                            id = v.getInt("id"),
                            title = v.getString("title"),
                            category = v.getString("category")
                        ))
                    }
                    _videogames.value = list
                }
            } catch (e: Exception) {
                // _videogames stays unchanged
            }
        }
    }

    fun createOffer(description: String, targetPlayers: Int, videogameId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = JSONObject().apply {
                    put("action", "CREATE_OFFER")
                    put("data", JSONObject().apply {
                        put("description", description)
                        put("targetPlayersCount", targetPlayers)
                        put("creator", JSONObject().apply {
                            put("id", userId)
                        })
                        put("videogame", JSONObject().apply {
                            put("id", videogameId)
                        })
                    })
                }
                val responseStr = SocketClient.send(request.toString())
                val response = JSONObject(responseStr)
                if (response.getString("action") == "CREATE_OFFER_SUCCESS") {
                    val data = response.getJSONObject("data")
                    val chatId = data.getInt("chat_id")
                    val chatName = data.getString("chat_name")
                    _createOfferState.value = CreateOfferState.Success(chatId, chatName)
                } else {
                    val message = response.getJSONObject("data").optString("message", "Failed to create offer")
                    _createOfferState.value = CreateOfferState.Error(message)
                }
            } catch (e: Exception) {
                _createOfferState.value = CreateOfferState.Error("Connection error: ${e.message}")
            }
        }
    }

    fun resetCreateOfferState() {
        _createOfferState.value = CreateOfferState.Idle
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    fun login(usernameInput: String, password: String) {
        // Para lanzarlo en un nuevo hilo secundario
        viewModelScope.launch(Dispatchers.IO) {
            _authState.value = AuthState.Loading
            try {
                // Aply para ahorrar crear objeto y luego hacer request.put() 2 veces
                val request = JSONObject().apply {
                    put("action", "LOGIN")
                    put("data", JSONObject().apply {
                        put("username", usernameInput)
                        put("password", password)
                    })
                }
                val responseStr = SocketClient.send(request.toString())
                if (responseStr.isEmpty()) {
                    _authState.value = AuthState.Error("The server did not respond, try again later")
                } else {
                    val response = JSONObject(responseStr)
                    when (response.getString("action")) {
                        "LOGIN_SUCCESS" -> {
                            val data = response.getJSONObject("data")
                            userId = data.getInt("user_id")
                            username = data.getString("username")
                            _authState.value = AuthState.LoginSuccess
                        }
                        else -> {
                            val message = response.getJSONObject("data").optString("message", "Login failed")
                            _authState.value = AuthState.Error(message)
                        }
                    }
                }
            } catch (e: SocketTimeoutException) {
                _authState.value = AuthState.Error("The server did not respond, try again later")
            } catch (e: JSONException) {
                _authState.value = AuthState.Error("Received an invalid response from the server")
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Connection error: ${e.message}")
            }
        }
    }

    fun signup(email: String, usernameInput: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _authState.value = AuthState.Loading
            try {
                // Aply para ahorrar crear objeto y luego hacer request.put() 2 veces
                val request = JSONObject().apply {
                    put("action", "SIGNUP")
                    put("data", JSONObject().apply {
                        put("email", email)
                        put("username", usernameInput)
                        put("password", password)
                    })
                }
                val responseStr = SocketClient.send(request.toString())
                if (responseStr.isEmpty()) {
                    _authState.value = AuthState.Error("The server did not respond, try again later")
                } else {
                    val response = JSONObject(responseStr)
                    when (response.getString("action")) {
                        "SIGNUP_SUCCESS" -> _authState.value = AuthState.SignupSuccess
                        else -> {
                            val message = response.getJSONObject("data").optString("message", "Signup failed")
                            _authState.value = AuthState.Error(message)
                        }
                    }
                }
            } catch (e: SocketTimeoutException) {
                _authState.value = AuthState.Error("The server did not respond, try again later")
            } catch (e: JSONException) {
                _authState.value = AuthState.Error("Received an invalid response from the server")
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Connection error: ${e.message}")
            }
        }
    }

    fun searchOffers(
        videogameId: Int? = null,
        category: String? = null,
        minPlayers: String? = null,
        maxPlayers: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val filters = JSONObject().apply {
                    if (videogameId != null) put("videogame_id", videogameId)
                    if (category != null) put("videogame_category", category)
                    if (minPlayers != null) put("min_current_players", minPlayers)
                    if (maxPlayers != null) put("max_target_players", maxPlayers)
                }
                val request = JSONObject().apply {
                    put("action", "SEARCH_OFFERS")
                    put("data", JSONObject().apply {
                        put("user_id", userId)
                        put("filters", filters)
                    })
                }
                val responseStr = SocketClient.send(request.toString())
                val response = JSONObject(responseStr)
                if (response.getString("action") == "SEARCH_OFFERS_SUCCESS") {
                    val offersArray = response.getJSONObject("data").getJSONArray("offers")
                    val offers = mutableListOf<Offer>()
                    for (i in 0 until offersArray.length()) {
                        val o = offersArray.getJSONObject(i)
                        offers.add(
                            Offer(
                                offerId = o.getInt("offer_id"),
                                description = o.getString("description"),
                                currentPlayers = o.getInt("current_players"),
                                targetPlayers = o.getInt("target_players"),
                                videogame = Videogame(o.getInt("videogame_id"), o.getString("videogame_title")),
                                creatorId = o.getInt("creator_id"),
                                creatorUsername = o.getString("creator_username")
                            )
                        )
                    }
                    _offers.value = offers
                }
            } catch (e: Exception) {
                // _offers se queda sin cambiar si falla
            } finally {
                _isLoading.value = false
            }
        }
    }
}
