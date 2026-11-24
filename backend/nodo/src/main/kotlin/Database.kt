package com.example

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

object NodeConfig {
    val nodeId: String = System.getenv("NODE_ID") ?: "norte"
    val dbUrl: String = System.getenv("DB_URL")
        ?: "jdbc:mysql://localhost:3308/social?allowPublicKeyRetrieval=true&useSSL=false"
    val dbUser: String = System.getenv("DB_USER") ?: "root"
    val dbPass: String = System.getenv("DB_PASS") ?: "root"
}

object Db {
    val dataSource: DataSource by lazy {
        HikariDataSource(HikariConfig().apply {
            jdbcUrl = NodeConfig.dbUrl
            username = NodeConfig.dbUser
            password = NodeConfig.dbPass
            maximumPoolSize = 5
            connectionTimeout = 3000
        })
    }

    fun ping(): Boolean = try {
        dataSource.connection.use { c -> c.createStatement().use { it.execute("SELECT 1") } }
        true
    } catch (e: Exception) {
        false
    }
}