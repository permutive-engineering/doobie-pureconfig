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

/** Configuration case class for a Doobie Transactor.
  *
  * Helps with creating `com.zaxxer.hikari.HikariConfig`, which in turn is used to create
  * `doobie.hikari.HikariTransactor`. See the method `HikariTransactor.fromHikariConfig`
  */
final case class DoobieConfig(
    settings: Config,
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
      settings, dataSource, newDataSourceProperties, healthCheckProperties, healthCheckRegistry, metricRegistry,
      metricsTrackerFactory, scheduledExecutor, threadFactory
    )
  }

}

object DoobieConfig {

  implicit val DoobieConfigConfigReader: ConfigReader[DoobieConfig] = deriveReader

}
