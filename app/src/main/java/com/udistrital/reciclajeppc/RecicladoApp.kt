package com.udistrital.reciclajeppc

import android.app.Application
import com.udistrital.reciclajeppc.data.fake.FakeAuthRepository
import com.udistrital.reciclajeppc.data.fake.FakeMessageRepository
import com.udistrital.reciclajeppc.data.fake.FakeRequestRepository
import com.udistrital.reciclajeppc.domain.repository.AuthRepository
import com.udistrital.reciclajeppc.domain.repository.MessageRepository
import com.udistrital.reciclajeppc.domain.repository.RequestRepository
import com.udistrital.reciclajeppc.core.session.SessionManager


/**
 * Contenedor de dependencias.
 * Cuando integremos Firebase, SOLO se cambian las tres clases de aquí.
 */
class AppContainer {
    val authRepository: AuthRepository by lazy { FakeAuthRepository() }
    val requestRepository: RequestRepository by lazy { FakeRequestRepository() }
    val messageRepository: MessageRepository by lazy { FakeMessageRepository() }
    val sessionManager: SessionManager by lazy { SessionManager(authRepository) }
}

class RecicladoApp : Application() {
    val container = AppContainer()
}