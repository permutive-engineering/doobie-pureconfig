/*
 * Copyright 2024 Permutive Ltd. <https://permutive.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package doobie.pureconfig

import scala.concurrent.duration._

import cats.syntax.all._

import doobie.enumerated.TransactionIsolation
import munit.FunSuite
import pureconfig.ConfigSource

class DoobieConfigSuite extends FunSuite {

  test("Ensure simple configuration can be created from HOCON") {
    val hocon =
      """settings.jdbc-url: "jdbc:postgresql://localhost:5001/foo"
        |data-source-properties.firstProperty: "firstValue"
        |data-source-properties.secondProperty: "secondValue"
        |""".stripMargin

    val config = ConfigSource.string(hocon).loadOrThrow[DoobieConfig]

    val expected = DoobieConfig(
      settings = DoobieConfig.Hikari(
        jdbcUrl = "jdbc:postgresql://localhost:5001/foo", catalog = None, connectionTimeout = 30.seconds,
        idleTimeout = 10.minutes, leakDetectionThreshold = Duration.Zero, maximumPoolSize = 10,
        maxLifetime = 30.minutes, minimumIdle = 10, password = None, poolName = None, username = None,
        validationTimeout = 5.seconds, allowPoolSuspension = false, autoCommit = true, connectionInitSql = None,
        connectionTestQuery = None, dataSourceClassName = None, dataSourceJNDI = None, driverClassName = None,
        initializationFailTimeout = 1.milliseconds, isolateInternalQueries = false, readOnly = false,
        registerMbeans = false, schema = None, transactionIsolation = None
      ),
      dataSourceProperties = Some(
        new java.util.Properties {
          put("firstProperty", "firstValue")
          put("secondProperty", "secondValue")
        }
      )
    )

    assertEquals(config, expected)
  }

  test("Ensure complete configuration can be created from HOCON") {
    val hocon =
      """settings.jdbc-url: "jdbc:postgresql://localhost:5001/foo"
        |settings.catalog: "catalog"
        |settings.connection-timeout: 1 minute
        |settings.idle-timeout: 2 minutes
        |settings.leak-detection-threshold: 3 minutes
        |settings.maximum-pool-size: 20
        |settings.max-lifetime: 4 minutes
        |settings.minimum-idle: 5
        |settings.password: "password"
        |settings.pool-name: "poolName"
        |settings.username: "username"
        |settings.validation-timeout: 6 minutes
        |settings.allow-pool-suspension: true
        |settings.auto-commit: false
        |settings.connection-init-sql: "connectionInitSql"
        |settings.connection-test-query: "connectionTestQuery"
        |settings.data-source-class-name: "dataSourceClassName"
        |settings.data-source-jndi: "dataSourceJNDI"
        |settings.driver-class-name: "driverClassName"
        |settings.initialization-fail-timeout: 7 minutes
        |settings.isolate-internal-queries: true
        |settings.read-only: true
        |settings.register-mbeans: true
        |settings.schema: "schema"
        |settings.transaction-isolation: "Serializable"
        |data-source-properties.firstProperty: "firstValue"
        |data-source-properties.secondProperty: "secondValue"
        |data-source-properties.thirdProperty: "thirdValue"
        |""".stripMargin

    val config = ConfigSource.string(hocon).loadOrThrow[DoobieConfig]

    val expected = DoobieConfig(
      settings = DoobieConfig.Hikari(
        jdbcUrl = "jdbc:postgresql://localhost:5001/foo", catalog = "catalog".some, connectionTimeout = 1.minute,
        idleTimeout = 2.minutes, leakDetectionThreshold = 3.minutes, maximumPoolSize = 20, maxLifetime = 4.minutes,
        minimumIdle = 5, password = "password".some, poolName = "poolName".some, username = "username".some,
        validationTimeout = 6.minutes, allowPoolSuspension = true, autoCommit = false,
        connectionInitSql = "connectionInitSql".some, connectionTestQuery = "connectionTestQuery".some,
        dataSourceClassName = "dataSourceClassName".some, dataSourceJNDI = "dataSourceJNDI".some,
        driverClassName = "driverClassName".some, initializationFailTimeout = 7.minutes, isolateInternalQueries = true,
        readOnly = true, registerMbeans = true, schema = "schema".some,
        transactionIsolation = TransactionIsolation.TransactionSerializable.some
      ),
      dataSourceProperties = Some(
        new java.util.Properties {
          put("firstProperty", "firstValue")
          put("secondProperty", "secondValue")
          put("thirdProperty", "thirdValue")
        }
      )
    )

    assertEquals(config, expected)
  }

  test("Properties in `data-source-properties` can be nulled in subsequent lines") {
    val hocon =
      """settings.jdbc-url: "jdbc:postgresql://localhost:5001/foo"
        |data-source-properties.firstProperty: "firstValue"
        |data-source-properties.secondProperty: "secondValue"
        |data-source-properties.secondProperty: null
        |""".stripMargin

    val config = ConfigSource.string(hocon).loadOrThrow[DoobieConfig]

    val expected = DoobieConfig(
      settings = DoobieConfig.Hikari("jdbc:postgresql://localhost:5001/foo"),
      dataSourceProperties = Some(
        new java.util.Properties {
          put("firstProperty", "firstValue")
        }
      )
    )

    assertEquals(config, expected)
  }

}
