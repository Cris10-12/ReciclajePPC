package com.udistrital.reciclajeppc.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.udistrital.reciclajeppc.AppContainer
import com.udistrital.reciclajeppc.core.constants.AppTexts
import com.udistrital.reciclajeppc.core.util.forRole
import com.udistrital.reciclajeppc.domain.model.ConnectionStatus
import com.udistrital.reciclajeppc.domain.repository.DuplicateDocumentException
import com.udistrital.reciclajeppc.ui.components.BottomNavBar
import com.udistrital.reciclajeppc.ui.components.bottomNavItemsFor
import com.udistrital.reciclajeppc.ui.screens.auth.LoginScreen
import com.udistrital.reciclajeppc.ui.screens.auth.RegisterScreen
import com.udistrital.reciclajeppc.ui.screens.connections.ConnectionsScreen
import com.udistrital.reciclajeppc.ui.screens.connections.ConnectionsTab
import com.udistrital.reciclajeppc.ui.screens.connections.ConnectionsViewModel
import com.udistrital.reciclajeppc.ui.screens.connections.UserConnectionsScreen
import com.udistrital.reciclajeppc.ui.screens.connections.UserConnectionsViewModel
import com.udistrital.reciclajeppc.ui.screens.favorites.FavoritesScreen
import com.udistrital.reciclajeppc.ui.screens.favorites.FavoritesViewModel
import com.udistrital.reciclajeppc.ui.screens.gallery.GalleryScreen
import com.udistrital.reciclajeppc.ui.screens.gallery.GalleryViewModel
import com.udistrital.reciclajeppc.ui.screens.generator.CreateRequestScreen
import com.udistrital.reciclajeppc.ui.screens.generator.CreateRequestViewModel
import com.udistrital.reciclajeppc.ui.screens.generator.GeneratorHomeScreen
import com.udistrital.reciclajeppc.ui.screens.generator.GeneratorHomeViewModel
import com.udistrital.reciclajeppc.ui.screens.generator.MyRequestsScreen
import com.udistrital.reciclajeppc.ui.screens.generator.MyRequestsViewModel
import com.udistrital.reciclajeppc.ui.screens.menu.MainMenuScreen
import com.udistrital.reciclajeppc.ui.screens.messages.ChatScreen
import com.udistrital.reciclajeppc.ui.screens.messages.ChatViewModel
import com.udistrital.reciclajeppc.ui.screens.messages.MessagesScreen
import com.udistrital.reciclajeppc.ui.screens.messages.MessagesViewModel
import com.udistrital.reciclajeppc.ui.screens.people.PeopleScreen
import com.udistrital.reciclajeppc.ui.screens.people.PeopleViewModel
import com.udistrital.reciclajeppc.ui.screens.profile.EditProfileScreen
import com.udistrital.reciclajeppc.ui.screens.profile.EditProfileViewModel
import com.udistrital.reciclajeppc.ui.screens.profile.ProfileScreen
import com.udistrital.reciclajeppc.ui.screens.profile.ProfileViewModel
import com.udistrital.reciclajeppc.ui.screens.profile.PublicProfileScreen
import com.udistrital.reciclajeppc.ui.screens.profile.PublicProfileViewModel
import com.udistrital.reciclajeppc.ui.screens.recommendations.RecommendScreen
import com.udistrital.reciclajeppc.ui.screens.recommendations.RecommendViewModel
import com.udistrital.reciclajeppc.ui.screens.recycler.AgendaScreen
import com.udistrital.reciclajeppc.ui.screens.recycler.AgendaViewModel
import com.udistrital.reciclajeppc.ui.screens.recycler.ExploreScreen
import com.udistrital.reciclajeppc.ui.screens.recycler.ExploreViewModel
import com.udistrital.reciclajeppc.ui.screens.recycler.RecyclerHomeScreen
import com.udistrital.reciclajeppc.ui.screens.recycler.RecyclerHomeViewModel
import com.udistrital.reciclajeppc.ui.screens.shared.HistoryScreen
import com.udistrital.reciclajeppc.ui.screens.shared.HistoryViewModel
import com.udistrital.reciclajeppc.ui.screens.shared.RequestDetailScreen
import com.udistrital.reciclajeppc.ui.screens.shared.RequestDetailViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@Composable
fun AppNavGraph(container: AppContainer) {
    val sessionManager = container.sessionManager
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val role by sessionManager.activeRole.collectAsState()
    val user by sessionManager.currentUser.collectAsState()
    val scope = rememberCoroutineScope()
    val myConversations by remember(user?.id) {
        user?.let { container.messageRepository.observeConversations(it.id) }
            ?: flowOf(emptyList())
    }.collectAsState(initial = emptyList())
    val allRequests by container.requestRepository
        .observeRequests()
        .collectAsState(initial = emptyList())
    val unreadMessages = user?.id?.let { myId ->
        myConversations.forRole(allRequests, myId, role).sumOf { it.unreadFor(myId) }
    } ?: 0

    val myConnections by remember(user?.id) {
        user?.let { container.socialRepository.observeConnections(it.id) }
            ?: flowOf(emptyList())
    }.collectAsState(initial = emptyList())
    val pendingRequests = myConnections.count {
        it.status == ConnectionStatus.PENDING && it.receiverId == user?.id
    }

    // Abre el perfil de una persona; si es el propio usuario, abre la pestaña Perfil limpia.
    val openUser: (String) -> Unit = { id ->
        if (id == user?.id) {
            navController.openOwnProfile(Routes.homeFor(role))
        } else {
            navController.navigate(Routes.publicProfile(id))
        }
    }

    val openMessages: () -> Unit = {
        navController.navigate(Routes.MESSAGES) { launchSingleTop = true }
    }

    Scaffold(
        bottomBar = {
            if (currentRoute in Routes.tabRoutes) {
                BottomNavBar(
                    items = bottomNavItemsFor(role),
                    currentRoute = currentRoute,
                    onItemClick = { route ->
                        navController.navigateToTab(route, Routes.homeFor(role))
                    },
                    badgeCounts = mapOf(Routes.PEOPLE to pendingRequests)
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(padding)
        ) {
            // ---------- Acceso ----------
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLogin = { email, password ->
                        scope.launch {
                            sessionManager.login(email, password)
                            navController.navigate(Routes.MAIN_MENU) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        }
                    },
                    onGoToRegister = { navController.navigate(Routes.REGISTER) }
                )
            }
            composable(Routes.REGISTER) {
                var registerError by remember { mutableStateOf<String?>(null) }

                RegisterScreen(
                    submitError = registerError,
                    onRegister = { name, email, documentType, documentNumber,
                                   phone, neighborhood, password ->
                        registerError = null
                        scope.launch {
                            sessionManager.register(
                                name, email, documentType, documentNumber,
                                phone, neighborhood, password
                            ).onSuccess {
                                navController.navigate(Routes.MAIN_MENU) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            }.onFailure { error ->
                                registerError = if (error is DuplicateDocumentException) {
                                    AppTexts.ERR_DOC_DUPLICATE
                                } else {
                                    AppTexts.ERR_REGISTER_GENERIC
                                }
                            }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.MAIN_MENU) {
                MainMenuScreen(
                    userName = user?.name?.substringBefore(" ").orEmpty(),
                    onSelectRole = { selected ->
                        sessionManager.setRole(selected)
                        navController.navigate(Routes.homeFor(selected)) {
                            popUpTo(Routes.MAIN_MENU) { inclusive = true }
                        }
                    },
                    onLogout = {
                        scope.launch {
                            sessionManager.logout()
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    }
                )
            }

            // ---------- Generador ----------
            composable(Routes.GENERATOR_HOME) {
                val viewModel: GeneratorHomeViewModel =
                    viewModel(factory = GeneratorHomeViewModel.factory(container))
                val state by viewModel.uiState.collectAsState()

                GeneratorHomeScreen(
                    state = state,
                    onUserClick = openUser,
                    unreadMessages = unreadMessages,
                    onMessages = openMessages,
                    onCreateRequest = { navController.navigate(Routes.CREATE_REQUEST) },
                    onRequestClick = { id -> navController.navigate(Routes.requestDetail(id)) },
                    onSeeAll = {
                        navController.navigateToTab(Routes.MY_REQUESTS, Routes.GENERATOR_HOME)
                    }
                )
            }
            composable(Routes.CREATE_REQUEST) {
                val viewModel: CreateRequestViewModel =
                    viewModel(factory = CreateRequestViewModel.factory(container))
                val state by viewModel.uiState.collectAsState()

                CreateRequestScreen(
                    state = state,
                    onMaterialToggle = viewModel::onMaterialToggle,
                    onWeightChange = viewModel::onWeightChange,
                    onAddressChange = viewModel::onAddressChange,
                    onNeighborhoodChange = viewModel::onNeighborhoodChange,
                    onAvailabilityChange = viewModel::onAvailabilityChange,
                    onNotesChange = viewModel::onNotesChange,
                    onSubmit = viewModel::submit,
                    onPublished = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.MY_REQUESTS) {
                val viewModel: MyRequestsViewModel =
                    viewModel(factory = MyRequestsViewModel.factory(container))
                val state by viewModel.uiState.collectAsState()

                MyRequestsScreen(
                    state = state,
                    onUserClick = openUser,
                    unreadMessages = unreadMessages,
                    onMessages = openMessages,
                    onFilterChange = viewModel::onFilterChange,
                    onCreateRequest = { navController.navigate(Routes.CREATE_REQUEST) },
                    onRequestClick = { id -> navController.navigate(Routes.requestDetail(id)) },
                    onHistory = { navController.navigate(Routes.HISTORY) }
                )
            }

            // ---------- Reciclador ----------
            composable(Routes.RECYCLER_HOME) {
                val viewModel: RecyclerHomeViewModel =
                    viewModel(factory = RecyclerHomeViewModel.factory(container))
                val state by viewModel.uiState.collectAsState()

                RecyclerHomeScreen(
                    state = state,
                    onUserClick = openUser,
                    unreadMessages = unreadMessages,
                    onMessages = openMessages,
                    onExplore = {
                        navController.navigateToTab(Routes.EXPLORE, Routes.RECYCLER_HOME)
                    },
                    onRequestClick = { id -> navController.navigate(Routes.requestDetail(id)) },
                    onSeeAgenda = {
                        navController.navigateToTab(Routes.AGENDA, Routes.RECYCLER_HOME)
                    }
                )
            }
            composable(Routes.EXPLORE) {
                val viewModel: ExploreViewModel =
                    viewModel(factory = ExploreViewModel.factory(container))
                val state by viewModel.uiState.collectAsState()

                ExploreScreen(
                    state = state,
                    onUserClick = openUser,
                    unreadMessages = unreadMessages,
                    onMessages = openMessages,
                    onMaterialToggle = viewModel::onMaterialToggle,
                    onClearFilter = viewModel::onClearFilter,
                    onRequestClick = { id -> navController.navigate(Routes.requestDetail(id)) }
                )
            }
            composable(Routes.AGENDA) {
                val viewModel: AgendaViewModel =
                    viewModel(factory = AgendaViewModel.factory(container))
                val state by viewModel.uiState.collectAsState()

                AgendaScreen(
                    state = state,
                    onUserClick = openUser,
                    unreadMessages = unreadMessages,
                    onMessages = openMessages,
                    onExplore = {
                        navController.navigateToTab(Routes.EXPLORE, Routes.RECYCLER_HOME)
                    },
                    onRequestClick = { id -> navController.navigate(Routes.requestDetail(id)) },
                    onHistory = { navController.navigate(Routes.HISTORY) }
                )
            }

            // ---------- Compartidas ----------
            composable(Routes.HISTORY) {
                val viewModel: HistoryViewModel =
                    viewModel(factory = HistoryViewModel.factory(container))
                val state by viewModel.uiState.collectAsState()

                HistoryScreen(
                    state = state,
                    onUserClick = openUser,
                    onFilterChange = viewModel::onFilterChange,
                    onRequestClick = { id -> navController.navigate(Routes.requestDetail(id)) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.REQUEST_DETAIL,
                arguments = listOf(navArgument("requestId") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("requestId").orEmpty()
                val viewModel: RequestDetailViewModel = viewModel(
                    key = "request-detail-$id",
                    factory = RequestDetailViewModel.factory(container, id)
                )
                val state by viewModel.uiState.collectAsState()

                RequestDetailScreen(
                    state = state,
                    onAccept = viewModel::accept,
                    onComplete = viewModel::complete,
                    onCancel = viewModel::cancel,
                    onUserClick = openUser,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onOpenChat = {
                        viewModel.openChat { conversationId ->
                            navController.navigate(Routes.chat(conversationId))
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.MESSAGES) {
                val viewModel: MessagesViewModel =
                    viewModel(factory = MessagesViewModel.factory(container))
                val state by viewModel.uiState.collectAsState()

                MessagesScreen(
                    state = state,
                    onConversationClick = { id -> navController.navigate(Routes.chat(id)) },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.CHAT,
                arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("conversationId").orEmpty()
                val viewModel: ChatViewModel = viewModel(
                    key = "chat-$id",
                    factory = ChatViewModel.factory(container, id)
                )
                val state by viewModel.uiState.collectAsState()

                ChatScreen(
                    state = state,
                    onDraftChange = viewModel::onDraftChange,
                    onSend = viewModel::send,
                    onUserClick = openUser,
                    onRequestClick = { requestId ->
                        navController.navigate(Routes.requestDetail(requestId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.PEOPLE) {
                val viewModel: PeopleViewModel =
                    viewModel(factory = PeopleViewModel.factory(container))
                val state by viewModel.uiState.collectAsState()

                PeopleScreen(
                    state = state,
                    unreadMessages = unreadMessages,
                    onMessages = openMessages,
                    onQueryChange = viewModel::onQueryChange,
                    onRoleFilterChange = viewModel::onRoleFilterChange,
                    onUserClick = openUser,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onConnections = { navController.navigate(Routes.connections()) },
                    onFavorites = { navController.navigate(Routes.FAVORITES) },
                    onRequests = {
                        navController.navigate(Routes.connections(Routes.TAB_RECEIVED))
                    }
                )
            }
            composable(Routes.PROFILE) {
                val viewModel: ProfileViewModel =
                    viewModel(factory = ProfileViewModel.factory(container))
                val state by viewModel.uiState.collectAsState()

                ProfileScreen(
                    state = state,
                    unreadMessages = unreadMessages,
                    onMessages = openMessages,
                    onEdit = { navController.navigate(Routes.EDIT_PROFILE) },
                    onGallery = {
                        state.user?.let { navController.navigate(Routes.gallery(it.id)) }
                    },
                    onConnections = { navController.navigate(Routes.connections()) },
                    onFavorites = { navController.navigate(Routes.FAVORITES) },
                    onHistory = { navController.navigate(Routes.HISTORY) },
                    onChangeRole = {
                        navController.navigate(Routes.MAIN_MENU) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    onLogout = {
                        scope.launch {
                            sessionManager.logout()
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(navController.graph.id) { inclusive = true }
                            }
                        }
                    }
                )
            }
            composable(Routes.EDIT_PROFILE) {
                val viewModel: EditProfileViewModel =
                    viewModel(factory = EditProfileViewModel.factory(container))
                val state by viewModel.uiState.collectAsState()

                EditProfileScreen(
                    state = state,
                    onNameChange = viewModel::onNameChange,
                    onPhoneChange = viewModel::onPhoneChange,
                    onNeighborhoodChange = viewModel::onNeighborhoodChange,
                    onBioChange = viewModel::onBioChange,
                    onAvatarChange = viewModel::onAvatarChange,
                    onSave = viewModel::save,
                    onSaved = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.GALLERY,
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("userId").orEmpty()
                val viewModel: GalleryViewModel = viewModel(
                    key = "gallery-$id",
                    factory = GalleryViewModel.factory(container, id)
                )
                val state by viewModel.uiState.collectAsState()

                GalleryScreen(
                    state = state,
                    onAddPhotos = viewModel::addPhotos,
                    onDeletePhoto = viewModel::deletePhoto,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.PUBLIC_PROFILE,
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("userId").orEmpty()
                val viewModel: PublicProfileViewModel = viewModel(
                    key = "public-profile-$id",
                    factory = PublicProfileViewModel.factory(container, id)
                )
                val state by viewModel.uiState.collectAsState()
                val inTrail =
                    navController.previousBackStackEntry?.destination?.route in profileTrailRoutes

                PublicProfileScreen(
                    state = state,
                    onBack = { navController.popBackStack() },
                    onToggleFavorite = viewModel::toggleFavorite,
                    onConnect = viewModel::sendRequest,
                    onAccept = viewModel::acceptRequest,
                    onReject = viewModel::rejectRequest,
                    onRemoveConnection = viewModel::removeConnection,
                    onRecommend = { navController.navigate(Routes.recommend(id)) },
                    onMessage = {
                        viewModel.openChat { conversationId ->
                            navController.navigate(Routes.chat(conversationId))
                        }
                    },
                    onGallery = { navController.navigate(Routes.gallery(id)) },
                    onConnectionsClick = { navController.navigate(Routes.userConnections(id)) },
                    onUserClick = openUser,
                    onCloseTrail = if (inTrail) ({ navController.closeProfileTrail() }) else null
                )
            }

            composable(
                route = Routes.RECOMMEND,
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("userId").orEmpty()
                val viewModel: RecommendViewModel = viewModel(
                    key = "recommend-$id",
                    factory = RecommendViewModel.factory(container, id)
                )
                val state by viewModel.uiState.collectAsState()

                RecommendScreen(
                    state = state,
                    onCommentChange = viewModel::onCommentChange,
                    onSubmit = viewModel::submit,
                    onRemove = viewModel::remove,
                    onDone = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.CONNECTIONS,
                arguments = listOf(
                    navArgument(Routes.ARG_TAB) {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { entry ->
                val initialTab =
                    if (entry.arguments?.getString(Routes.ARG_TAB) == Routes.TAB_RECEIVED) {
                        ConnectionsTab.RECEIVED
                    } else {
                        ConnectionsTab.CONNECTED
                    }
                val viewModel: ConnectionsViewModel = viewModel(
                    factory = ConnectionsViewModel.factory(container, initialTab)
                )
                val state by viewModel.uiState.collectAsState()

                ConnectionsScreen(
                    state = state,
                    onTabChange = viewModel::onTabChange,
                    onAccept = viewModel::accept,
                    onReject = viewModel::reject,
                    onCancelSent = viewModel::cancelSent,
                    onRemove = viewModel::remove,
                    onUserClick = openUser,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.FAVORITES) {
                val viewModel: FavoritesViewModel =
                    viewModel(factory = FavoritesViewModel.factory(container))
                val state by viewModel.uiState.collectAsState()

                FavoritesScreen(
                    state = state,
                    onUserClick = openUser,
                    onRemoveFavorite = viewModel::removeFavorite,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.USER_CONNECTIONS,
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("userId").orEmpty()
                val viewModel: UserConnectionsViewModel = viewModel(
                    key = "user-connections-$id",
                    factory = UserConnectionsViewModel.factory(container, id)
                )
                val state by viewModel.uiState.collectAsState()
                val inTrail =
                    navController.previousBackStackEntry?.destination?.route in profileTrailRoutes

                UserConnectionsScreen(
                    state = state,
                    onUserClick = openUser,
                    onBack = { navController.popBackStack() },
                    onCloseTrail = if (inTrail) ({ navController.closeProfileTrail() }) else null
                )
            }
        }
    }
}

/** Cambia de pestaña sin acumular pantallas ni perder el estado. */
private fun NavHostController.navigateToTab(route: String, homeRoute: String) {
    navigate(route) {
        popUpTo(homeRoute) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/** Pantallas que forman una cadena de perfiles y listas de conexiones. */
private val profileTrailRoutes = setOf(Routes.PUBLIC_PROFILE, Routes.USER_CONNECTIONS)

/** Abre la pestaña Perfil desde cualquier pantalla, descartando lo recorrido. */
private fun NavHostController.openOwnProfile(homeRoute: String) {
    navigate(Routes.PROFILE) {
        popUpTo(homeRoute) { saveState = false }
        launchSingleTop = true
    }
}

/** Sale de la cadena de perfiles y vuelve a la pantalla donde empezó. */
private fun NavHostController.closeProfileTrail() {
    var guard = 0
    while (currentDestination?.route in profileTrailRoutes && guard < 50) {
        if (!popBackStack()) break
        guard++
    }
}