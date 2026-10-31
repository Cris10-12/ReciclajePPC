package com.udistrital.reciclajeppc.data.fake

import com.udistrital.reciclajeppc.domain.model.*

object FakeData {

    val currentUser = User(
        id = "u1",
        name = "Cristian Ramírez",
        email = "cristian@correo.com",
        phone = "300 123 4567",
        neighborhood = "Chapinero"
    )

    val requests: List<RecycleRequest> = listOf(
        RecycleRequest("r1", "u2", "Panadería El Trigal", MaterialType.CARDBOARD, 25.0,
            "Cra 13 #54-20", "Chapinero", "Lun a Vie, 8:00 - 11:00 a.m.",
            "Cajas aplastadas y amarradas.", RequestStatus.AVAILABLE),
        RecycleRequest("r2", "u3", "Restaurante La Esquina", MaterialType.GLASS, 40.0,
            "Cl 63 #9-15", "Chapinero", "Todos los días, 3:00 - 5:00 p.m.",
            "Botellas limpias, sin tapa.", RequestStatus.AVAILABLE),
        RecycleRequest("r3", "u4", "María Fernanda Gómez", MaterialType.PLASTIC, 8.5,
            "Cra 7 #72-41", "Usaquén", "Sábados, 9:00 a.m. - 12:00 m.",
            "Botellas PET y envases.", RequestStatus.AVAILABLE),
        RecycleRequest("r4", "u5", "Oficinas Andina S.A.S.", MaterialType.PAPER, 60.0,
            "Av. 19 #104-30", "Usaquén", "Martes y jueves, 2:00 - 4:00 p.m.",
            "Papel de archivo, sin carpetas.", RequestStatus.AVAILABLE),
        RecycleRequest("r5", "u6", "Taller Metálicas Soto", MaterialType.METAL, 120.0,
            "Cl 13 #68-12", "Puente Aranda", "Lun a Sáb, 7:00 a.m. - 5:00 p.m.",
            "Retal de aluminio y cobre.", RequestStatus.AVAILABLE),
        RecycleRequest("r6", "u7", "Colegio San Rafael", MaterialType.PLASTIC, 35.0,
            "Cra 30 #45-03", "Teusaquillo", "Viernes, 1:00 - 3:00 p.m.",
            "Botellas recolectadas en jornada escolar.", RequestStatus.ACCEPTED, "Carlos Pineda"),
        RecycleRequest("r7", "u1", "Cristian Ramírez", MaterialType.ELECTRONIC, 12.0,
            "Cl 45 #16-28", "Chapinero", "Domingos, 10:00 a.m. - 1:00 p.m.",
            "Dos monitores y un teclado dañados.", RequestStatus.ACCEPTED, "Ana Torres"),
        RecycleRequest("r8", "u8", "Tienda Don Pedro", MaterialType.CARDBOARD, 18.0,
            "Cra 24 #40-10", "Teusaquillo", "Lun a Vie, 6:00 - 8:00 a.m.",
            "Cajas de mercancía.", RequestStatus.COMPLETED, "Carlos Pineda"),
        RecycleRequest("r9", "u1", "Cristian Ramírez", MaterialType.PAPER, 15.0,
            "Cl 45 #16-28", "Chapinero", "Sábados, 8:00 - 10:00 a.m.",
            "Periódicos y revistas.", RequestStatus.COMPLETED, "Ana Torres"),
        RecycleRequest("r10", "u9", "Café Origen", MaterialType.GLASS, 22.0,
            "Cl 85 #12-40", "Chicó", "Todos los días, 4:00 - 6:00 p.m.",
            "Frascos y botellas.", RequestStatus.CANCELLED)
    )

    val conversations: List<Conversation> = listOf(
        // Como GENERADOR hablo con recicladores
        Conversation(
            "c1", "Ana Torres", "Perfecto, paso el domingo a las 11.", "10:42 a.m.", 1,
            listOf(
                ChatMessage("m1", "Hola, vi tu solicitud de electrónicos.", false, "10:30 a.m."),
                ChatMessage("m2", "¡Hola! Sí, están listos para recoger.", true, "10:35 a.m."),
                ChatMessage("m3", "Perfecto, paso el domingo a las 11.", false, "10:42 a.m.")
            ),
            myRole = UserRole.GENERATOR
        ),
        Conversation(
            "c2", "Carlos Pineda", "Gracias por la recolección.", "Ayer", 0,
            listOf(
                ChatMessage("m4", "Ya recogí las cajas, gracias.", false, "Ayer"),
                ChatMessage("m5", "Gracias por la recolección.", true, "Ayer")
            ),
            myRole = UserRole.GENERATOR
        ),
        // Como RECICLADOR hablo con generadores
        Conversation(
            "c3", "Panadería El Trigal", "¿Puede pasar antes de las 10?", "Lunes", 2,
            listOf(
                ChatMessage("m6", "Buenos días, vi que aceptó la solicitud.", false, "Lunes"),
                ChatMessage("m7", "¿Puede pasar antes de las 10?", false, "Lunes")
            ),
            myRole = UserRole.RECYCLER
        ),
        Conversation(
            "c4", "Restaurante La Esquina", "Las botellas están en la entrada.", "9:15 a.m.", 1,
            listOf(
                ChatMessage("m8", "Hola, ¿a qué hora pasa por las botellas?", true, "9:00 a.m."),
                ChatMessage("m9", "Las botellas están en la entrada.", false, "9:15 a.m.")
            ),
            myRole = UserRole.RECYCLER
        )
    )

    fun requestById(id: String): RecycleRequest? = requests.firstOrNull { it.id == id }
}