package com.udistrital.reciclajeppc.data.fake

import com.udistrital.reciclajeppc.domain.model.ChatMessage
import com.udistrital.reciclajeppc.domain.model.Connection
import com.udistrital.reciclajeppc.domain.model.ConnectionStatus
import com.udistrital.reciclajeppc.domain.model.Conversation
import com.udistrital.reciclajeppc.domain.model.DocumentType
import com.udistrital.reciclajeppc.domain.model.Favorite
import com.udistrital.reciclajeppc.domain.model.MaterialType
import com.udistrital.reciclajeppc.domain.model.Participant
import com.udistrital.reciclajeppc.domain.model.Photo
import com.udistrital.reciclajeppc.domain.model.Recommendation
import com.udistrital.reciclajeppc.domain.model.RecycleRequest
import com.udistrital.reciclajeppc.domain.model.RequestStatus
import com.udistrital.reciclajeppc.domain.model.User
import com.udistrital.reciclajeppc.domain.model.UserRole

object FakeData {

    private const val DAY_MS = 86_400_000L
    private const val HOUR_MS = 3_600_000L
    private const val MIN_MS = 60_000L
    private val now = System.currentTimeMillis()

    val currentUser = User(
        id = "u1",
        name = "Cristian Ramírez",
        email = "cristian@correo.com",
        documentType = DocumentType.CC,
        documentNumber = "1012345678",
        phone = "300 123 4567",
        neighborhood = "Chapinero",
        bio = "Separo mis residuos en casa y me gusta que terminen en buenas manos.",
        roles = listOf(UserRole.GENERATOR, UserRole.RECYCLER)
    )

    val users: List<User> = listOf(
        currentUser,
        User(
            id = "u2", name = "Panadería El Trigal", email = "trigal@correo.com",
            documentType = DocumentType.OTHER, documentNumber = "9001234561",
            phone = "601 345 6789", neighborhood = "Chapinero",
            bio = "Panadería de barrio. Separamos las cajas a diario para que las recojan.",
            roles = listOf(UserRole.GENERATOR)
        ),
        User(
            id = "u3", name = "Restaurante La Esquina", email = "laesquina@correo.com",
            documentType = DocumentType.OTHER, documentNumber = "9002345672",
            phone = "601 456 7890", neighborhood = "Chapinero",
            bio = "Generamos vidrio todos los días y buscamos a quien lo recolecte con regularidad.",
            roles = listOf(UserRole.GENERATOR)
        ),
        User(
            id = "u4", name = "María Fernanda Gómez", email = "mafe@correo.com",
            documentType = DocumentType.CC, documentNumber = "52123456",
            phone = "310 222 3344", neighborhood = "Usaquén",
            bio = "Reciclo en casa y los fines de semana recojo material en mi barrio.",
            roles = listOf(UserRole.GENERATOR, UserRole.RECYCLER)
        ),
        User(
            id = "u5", name = "Oficinas Andina S.A.S.", email = "andina@correo.com",
            documentType = DocumentType.OTHER, documentNumber = "9003456783",
            phone = "601 567 8901", neighborhood = "Usaquén",
            bio = "Gestión de papel de archivo para oficinas.",
            roles = listOf(UserRole.GENERATOR)
        ),
        User(
            id = "u6", name = "Taller Metálicas Soto", email = "soto@correo.com",
            documentType = DocumentType.OTHER, documentNumber = "9004567894",
            phone = "601 678 9012", neighborhood = "Puente Aranda",
            bio = "Taller de metalmecánica. Tenemos retal de aluminio y cobre cada semana.",
            roles = listOf(UserRole.GENERATOR)
        ),
        User(
            id = "u7", name = "Colegio San Rafael", email = "sanrafael@correo.com",
            documentType = DocumentType.OTHER, documentNumber = "8605678905",
            phone = "601 789 0123", neighborhood = "Teusaquillo",
            bio = "Jornadas escolares de reciclaje con los estudiantes.",
            roles = listOf(UserRole.GENERATOR)
        ),
        User(
            id = "u8", name = "Tienda Don Pedro", email = "donpedro@correo.com",
            documentType = DocumentType.CC, documentNumber = "79234567",
            phone = "320 333 4455", neighborhood = "Teusaquillo",
            bio = "Tienda de barrio con cajas de mercancía todos los días.",
            roles = listOf(UserRole.GENERATOR)
        ),
        User(
            id = "u9", name = "Café Origen", email = "origen@correo.com",
            documentType = DocumentType.OTHER, documentNumber = "9006789016",
            phone = "601 890 1234", neighborhood = "Chicó",
            bio = "Cafetería de especialidad. Frascos y botellas listos para reciclar.",
            roles = listOf(UserRole.GENERATOR)
        ),
        User(
            id = "u10", name = "Ana Torres", email = "ana@correo.com",
            documentType = DocumentType.CC, documentNumber = "1020456789",
            phone = "311 444 5566", neighborhood = "Chapinero",
            bio = "Recicladora de oficio. Me especializo en electrónicos y papel.",
            roles = listOf(UserRole.RECYCLER, UserRole.GENERATOR)
        ),
        User(
            id = "u11", name = "Carlos Pineda", email = "carlos@correo.com",
            documentType = DocumentType.CC, documentNumber = "80345678",
            phone = "315 555 6677", neighborhood = "Teusaquillo",
            bio = "Recolecto cartón y plástico en Teusaquillo y alrededores.",
            roles = listOf(UserRole.RECYCLER)
        )
    )

    val connections: List<Connection> = listOf(
        Connection("cn1", "u10", "u1", ConnectionStatus.ACCEPTED, now - 30 * DAY_MS, now - 29 * DAY_MS),
        Connection("cn2", "u1", "u11", ConnectionStatus.ACCEPTED, now - 20 * DAY_MS, now - 19 * DAY_MS),
        Connection("cn3", "u4", "u1", ConnectionStatus.PENDING, now - 1 * DAY_MS),
        Connection("cn4", "u1", "u3", ConnectionStatus.PENDING, now - 2 * DAY_MS),
        Connection("cn5", "u10", "u11", ConnectionStatus.ACCEPTED, now - 40 * DAY_MS, now - 39 * DAY_MS),
        Connection("cn6", "u1", "u6", ConnectionStatus.REJECTED, now - 15 * DAY_MS, now - 14 * DAY_MS)
    )

    val favorites: List<Favorite> = listOf(
        Favorite("fv1", "u1", "u10", now - 10 * DAY_MS),
        Favorite("fv2", "u1", "u3", now - 5 * DAY_MS),
        Favorite("fv3", "u10", "u2", now - 8 * DAY_MS)
    )

    val recommendations: List<Recommendation> = listOf(
        Recommendation(
            "rc1", "u1", "u10",
            "Muy puntual y cuidadosa con los equipos electrónicos. La recomiendo totalmente.",
            now - 12 * DAY_MS
        ),
        Recommendation(
            "rc2", "u11", "u10",
            "Hemos trabajado juntos en varias rutas y siempre cumple con los horarios acordados.",
            now - 25 * DAY_MS
        ),
        Recommendation(
            "rc3", "u10", "u1",
            "Siempre deja el material limpio y separado. Da gusto coordinar con él.",
            now - 9 * DAY_MS
        ),
        Recommendation(
            "rc4", "u1", "u11",
            "Atento y rápido al responder los mensajes. Muy buena experiencia.",
            now - 6 * DAY_MS
        )
    )

    val photos: List<Photo> = emptyList()

    val requests: List<RecycleRequest> = listOf(
        RecycleRequest("r1", "u2", MaterialType.CARDBOARD, 25.0,
            "Cra 13 #54-20", "Chapinero", "Lun a Vie, 8:00 - 11:00 a.m.",
            "Cajas aplastadas y amarradas.", RequestStatus.ACCEPTED, "u1"),
        RecycleRequest("r2", "u3", MaterialType.GLASS, 40.0,
            "Cl 63 #9-15", "Chapinero", "Todos los días, 3:00 - 5:00 p.m.",
            "Botellas limpias, sin tapa.", RequestStatus.ACCEPTED, "u1"),
        RecycleRequest("r3", "u4", MaterialType.PLASTIC, 8.5,
            "Cra 7 #72-41", "Usaquén", "Sábados, 9:00 a.m. - 12:00 m.",
            "Botellas PET y envases.", RequestStatus.AVAILABLE),
        RecycleRequest("r4", "u5", MaterialType.PAPER, 60.0,
            "Av. 19 #104-30", "Usaquén", "Martes y jueves, 2:00 - 4:00 p.m.",
            "Papel de archivo, sin carpetas.", RequestStatus.AVAILABLE),
        RecycleRequest("r5", "u6", MaterialType.METAL, 120.0,
            "Cl 13 #68-12", "Puente Aranda", "Lun a Sáb, 7:00 a.m. - 5:00 p.m.",
            "Retal de aluminio y cobre.", RequestStatus.AVAILABLE),
        RecycleRequest("r6", "u7", MaterialType.PLASTIC, 35.0,
            "Cra 30 #45-03", "Teusaquillo", "Viernes, 1:00 - 3:00 p.m.",
            "Botellas recolectadas en jornada escolar.", RequestStatus.ACCEPTED, "u11"),
        RecycleRequest("r7", "u1", MaterialType.ELECTRONIC, 12.0,
            "Cl 45 #16-28", "Chapinero", "Domingos, 10:00 a.m. - 1:00 p.m.",
            "Dos monitores y un teclado dañados.", RequestStatus.ACCEPTED, "u10"),
        RecycleRequest("r8", "u8", MaterialType.CARDBOARD, 18.0,
            "Cra 24 #40-10", "Teusaquillo", "Lun a Vie, 6:00 - 8:00 a.m.",
            "Cajas de mercancía.", RequestStatus.COMPLETED, "u11"),
        RecycleRequest("r9", "u1", MaterialType.PAPER, 15.0,
            "Cl 45 #16-28", "Chapinero", "Sábados, 8:00 - 10:00 a.m.",
            "Periódicos y revistas.", RequestStatus.COMPLETED, "u10"),
        RecycleRequest("r10", "u9", MaterialType.GLASS, 22.0,
            "Cl 85 #12-40", "Chicó", "Todos los días, 4:00 - 6:00 p.m.",
            "Frascos y botellas.", RequestStatus.CANCELLED)
    )

    val conversations: List<Conversation> = listOf(
        Conversation(
            id = "c1",
            requestId = "r7",
            participants = listOf(
                Participant("u1", now - 3 * HOUR_MS + 5 * MIN_MS),
                Participant("u10", now - HOUR_MS)
            ),
            messages = listOf(
                ChatMessage("m1", "u10", "Hola, vi tu solicitud de electrónicos.", now - 3 * HOUR_MS),
                ChatMessage("m2", "u1", "¡Hola! Sí, están listos para recoger.", now - 3 * HOUR_MS + 5 * MIN_MS),
                ChatMessage("m3", "u10", "Perfecto, paso el domingo a las 11.", now - HOUR_MS)
            )
        ),
        Conversation(
            id = "c2",
            requestId = "r9",
            participants = listOf(
                Participant("u1", now - DAY_MS),
                Participant("u10", now - DAY_MS)
            ),
            messages = listOf(
                ChatMessage("m4", "u10", "Ya recogí los periódicos, gracias.", now - DAY_MS - 10 * MIN_MS),
                ChatMessage("m5", "u1", "Gracias por la recolección.", now - DAY_MS)
            )
        ),
        Conversation(
            id = "c3",
            requestId = "r1",
            participants = listOf(
                Participant("u1", now - 3 * DAY_MS),
                Participant("u2", now - 2 * DAY_MS + 2 * MIN_MS)
            ),
            messages = listOf(
                ChatMessage("m6", "u2", "Buenos días, vi que aceptó la solicitud.", now - 2 * DAY_MS),
                ChatMessage("m7", "u2", "¿Puede pasar antes de las 10?", now - 2 * DAY_MS + 2 * MIN_MS)
            )
        ),
        Conversation(
            id = "c4",
            requestId = "r2",
            participants = listOf(
                Participant("u1", now - 5 * HOUR_MS),
                Participant("u3", now - 5 * HOUR_MS + 15 * MIN_MS)
            ),
            messages = listOf(
                ChatMessage("m8", "u1", "Hola, ¿a qué hora pasa por las botellas?", now - 5 * HOUR_MS),
                ChatMessage("m9", "u3", "Las botellas están en la entrada.", now - 5 * HOUR_MS + 15 * MIN_MS)
            )
        )
    )

    fun requestById(id: String): RecycleRequest? = requests.firstOrNull { it.id == id }
}