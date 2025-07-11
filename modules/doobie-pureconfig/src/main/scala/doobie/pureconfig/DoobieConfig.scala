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

import java.util.Properties
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ThreadFactory

import cats.effect.kernel.Sync
import cats.syntax.all._

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.metrics.MetricsTrackerFactory
import doobie.hikari.Config
import javax.sql.DataSource
import pureconfig.ConfigReader
import pureconfig.generic.semiauto.deriveReader
import scala.concurrent.duration._
import doobie.enumerated.TransactionIsolation

/** Configuration case class for a Doobie Transactor.
  *
  * Helps with creating `com.zaxxer.hikari.HikariConfig`, which in turn is used to create
  * `doobie.hikari.HikariTransactor`. See the method `HikariTransactor.fromHikariConfig`
  */
final case class DoobieConfig(
    settings: DoobieConfig.Hikari,
    dataSourceProperties: Option[Properties] = None
) {

  /** Adds a new data-source property to this configuration.
    *
    * @param key
    *   the property key
    * @param value
    *   the property value
    * @return
    *   the config object with the updated data-source properties
    */
  def withDataSourceProperty(key: String, value: String): DoobieConfig = {
    val newProperties = new Properties {
      dataSourceProperties.foreach(putAll(_))
      put(key, value)
    }
    copy(dataSourceProperties = newProperties.some)
  }

  /** Transforms this object into a valid `com.zaxxer.hikari.HikariConfig`, which in turn is used to create
    * `doobie.hikari.HikariTransactor`. See the method `HikariTransactor.fromHikariConfig`
    */
  def makeHikariConfig[F[_]: Sync](
      dataSource: Option[DataSource] = None,
      dataSourceExtraProperties: Option[Properties] = None,
      healthCheckProperties: Option[Properties] = None,
      healthCheckRegistry: Option[Object] = None,
      metricRegistry: Option[Object] = None,
      metricsTrackerFactory: Option[MetricsTrackerFactory] = None,
      scheduledExecutor: Option[ScheduledExecutorService] = None,
      threadFactory: Option[ThreadFactory] = None
  ): F[HikariConfig] = {
    val newDataSourceProperties =
      if (dataSourceProperties.isEmpty && dataSourceExtraProperties.isEmpty) None
      else
        new Properties() {
          dataSourceProperties.foreach(putAll(_))
          dataSourceExtraProperties.foreach(putAll(_))
        }.some

    Config.makeHikariConfig[F](
      settings.asHikariConfig, dataSource, newDataSourceProperties, healthCheckProperties, healthCheckRegistry,
      metricRegistry, metricsTrackerFactory, scheduledExecutor, threadFactory
    )
  }

}

object DoobieConfig {

  final case class Hikari(
      jdbcUrl: String,
      catalog: Option[String] = None,
      connectionTimeout: Duration = 30.seconds,
      idleTimeout: Duration = 10.minutes,
      leakDetectionThreshold: Duration = Duration.Zero,
      maximumPoolSize: Int = 10,
      maxLifetime: Duration = 30.minutes,
      minimumIdle: Int = 10,
      password: Option[String] = None,
      poolName: Option[String] = None,
      username: Option[String] = None,
      validationTimeout: Duration = 5.seconds,
      allowPoolSuspension: Boolean = false,
      autoCommit: Boolean = true,
      connectionInitSql: Option[String] = None,
      connectionTestQuery: Option[String] = None,
      dataSourceClassName: Option[String] = None,
      dataSourceJNDI: Option[String] = None,
      driverClassName: Option[String] = None,
      initializationFailTimeout: Duration = 1.millisecond,
      isolateInternalQueries: Boolean = false,
      readOnly: Boolean = false,
      registerMbeans: Boolean = false,
      schema: Option[String] = None,
      transactionIsolation: Option[TransactionIsolation] = None
  ) {

    def asHikariConfig: Config = Config(jdbcUrl, catalog, connectionTimeout, idleTimeout, leakDetectionThreshold,
      maximumPoolSize, maxLifetime, minimumIdle, password, poolName, username, validationTimeout, allowPoolSuspension,
      autoCommit, connectionInitSql, connectionTestQuery, dataSourceClassName, dataSourceJNDI, driverClassName,
      initializationFailTimeout, isolateInternalQueries, readOnly, registerMbeans, schema, transactionIsolation)

  }

  object Hikari {

    implicit val HikariConfigConfigReader: ConfigReader[Hikari] = deriveReader

  }

  implicit val DoobieConfigConfigReader: ConfigReader[DoobieConfig] = deriveReader

}
