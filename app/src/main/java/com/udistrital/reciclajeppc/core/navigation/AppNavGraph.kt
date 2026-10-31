package com.udistrital.reciclajeppc.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.udistrital.reciclajeppc.ui.components.BottomNavBar
import com.udistrital.reciclajeppc.ui.components.bottomNavItemsFor
import com.udistrital.reciclajeppc.ui.screens.auth.LoginScreen
import com.udistrital.reciclajeppc.ui.screens.auth.RegisterScreen
import com.udistrital.reciclajeppc.ui.screens.generator.GeneratorHomeScreen
import com.udistrital.reciclajeppc.ui.screens.generator.GeneratorHomeViewModel
import com.udistrital.reciclajeppc.ui.screens.menu.MainMenuScreen
import kotlinx.coroutines.launch
import com.udistrital.reciclajeppc.ui.screens.generator.CreateRequestScreen
import com.udistrital.reciclajeppc.ui.screens.generator.CreateRequestViewModel
import com.udistrital.reciclajeppc.ui.screens.recycler.RecyclerHomeScreen
import com.udistrital.reciclajeppc.ui.screens.recycler.RecyclerHomeViewModel
import com.udistrital.reciclajeppc.ui.screens.recycler.ExploreScreen
import com.udistrital.reciclajeppc.ui.screens.recycler.ExploreViewModel
import com.udistrital.reciclajeppc.ui.screens.shared.RequestDetailScreen
import com.udistrital.reciclajeppc.ui.screens.shared.RequestDetailViewModel
import com.udistrital.reciclajeppc.ui.screens.messages.ChatScreen
import com.udistrital.reciclajeppc.ui.screens.messages.ChatViewModel
import com.udistrital.reciclajeppc.ui.screens.messages.MessagesScreen
import com.udistrital.reciclajeppc.ui.screens.messages.MessagesViewModel
import com.udistrital.reciclajeppc.ui.screens.generator.MyRequestsScreen
import com.udistrital.reciclajeppc.ui.screens.generator.MyRequestsViewModel
import com.udistrital.reciclajeppc.ui.screens.recycler.AgendaScreen
import com.udistrital.reciclajeppc.ui.screens.recycler.AgendaViewModel
import com.udistrital.reciclajeppc.ui.screens.shared.HistoryScreen
import com.udistrital.reciclajeppc.ui.screens.shared.HistoryViewModel
import com.udistrital.reciclajeppc.ui.screens.profile.ProfileScreen
import com.udistrital.reciclajeppc.ui.screens.profile.ProfileViewModel

@Composable
fun AppNavGraph(container: AppContainer) {
    val sessionManager = container.sessionManager
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val role by sessionManager.activeRole.collectAsState()
    val user by sessionManager.currentUser.collectAsState()
    val scope = rememberCoroutineScope()
    val conversations by container.messageRepository
        .observeConversations()
        .collectAsState(initial = emptyList())
    val unreadMessages = conversations
        .filter { it.myRole == role }
        .sumOf { it.unread }


    Scaffold(
        bottomBar = {
            if (currentRoute in Routes.tabRoutes) {
                BottomNavBar(
                    items = bottomNavItemsFor(role),
                    currentRoute = currentRoute,
                    onItemClick = { route ->
                        navController.navigateToTab(route, Routes.homeFor(role))
                    },
                    badgeCounts = mapOf(Routes.MESSAGES to unreadMessages)
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
                RegisterScreen(
                    onRegister = { name, email, phone, neighborhood, password ->
                        scope.launch {
                            sessionManager.register(name, email, phone, neighborhood, password)
                            navController.navigate(Routes.MAIN_MENU) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
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
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.MESSAGES) {
                val viewModel: MessagesViewModel =
                    viewModel(factory = MessagesViewModel.factory(container))
                val state by viewModel.uiState.collectAsState()

                MessagesScreen(
                    state = state,
                    onConversationClick = { id -> navController.navigate(Routes.chat(id)) }
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
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Routes.PROFILE) {
                val viewModel: ProfileViewModel =
                    viewModel(factory = ProfileViewModel.factory(container))
                val state by viewModel.uiState.collectAsState()

                ProfileScreen(
                    state = state,
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