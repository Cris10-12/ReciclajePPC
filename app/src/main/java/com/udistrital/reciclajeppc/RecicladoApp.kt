package com.udistrital.reciclajeppc

import android.app.Application
import com.udistrital.reciclajeppc.core.session.SessionManager
import com.udistrital.reciclajeppc.data.fake.FakeAuthRepository
import com.udistrital.reciclajeppc.data.fake.FakeMessageRepository
import com.udistrital.reciclajeppc.data.fake.FakePhotoRepository
import com.udistrital.reciclajeppc.data.fake.FakeRequestRepository
import com.udistrital.reciclajeppc.data.fake.FakeSocialRepository
import com.udistrital.reciclajeppc.data.fake.FakeUserRepository
import com.udistrital.reciclajeppc.domain.repository.AuthRepository
import com.udistrital.reciclajeppc.domain.repository.MessageRepository
import com.udistrital.reciclajeppc.domain.repository.PhotoRepository
import com.udistrital.reciclajeppc.domain.repository.RequestRepository
import com.udistrital.reciclajeppc.domain.repository.SocialRepository
import com.udistrital.reciclajeppc.domain.repository.UserRepository

/**
 * Contenedor de dependencias.
 * Cuando integremos Firebase, SOLO se cambian las implementaciones de aquí.
 */
class AppContainer {
    // El repositorio de autenticación ficticio necesita registrar usuarios en el mismo
    // almacén que consulta el de usuarios, por eso comparten instancia.
    private val fakeUserRepository = FakeUserRepository()

    val userRepository: UserRepository = fakeUserRepository
    val authRepository: AuthRepository by lazy { FakeAuthRepository(fakeUserRepository) }
    val requestRepository: RequestRepository by lazy { FakeRequestRepository() }
    val messageRepository: MessageRepository by lazy { FakeMessageRepository(requestRepository) }
    val socialRepository: SocialRepository by lazy { FakeSocialRepository() }
    val photoRepository: PhotoRepository by lazy { FakePhotoRepository() }
    val sessionManager: SessionManager by lazy { SessionManager(authRepository, userRepository) }
}

class RecicladoApp : Application() {
    val container = AppContainer()
}