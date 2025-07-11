/*
 * Copyright 2024-2025 Permutive Ltd. <https://permutive.com>
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

package doobie

import java.util.Properties

import cats.syntax.all._

import _root_.pureconfig.ConfigReader
import _root_.pureconfig.error.CannotConvert
import doobie.enumerated.TransactionIsolation
import doobie.enumerated.TransactionIsolation._

package object pureconfig {

  implicit val TransactionIsolationConfigReader: ConfigReader[TransactionIsolation] = ConfigReader[String].emap {
    case "None"            => TransactionNone.asRight
    case "ReadUncommitted" => TransactionReadUncommitted.asRight
    case "ReadCommitted"   => TransactionReadCommitted.asRight
    case "RepeatableRead"  => TransactionRepeatableRead.asRight
    case "Serializable"    => TransactionSerializable.asRight
    case unknown           => CannotConvert(unknown, "TransactionIsolation", "unknown value").asLeft
  }

  @SuppressWarnings(Array("scalafix:Disable.toString"))
  implicit val PropertiesConfigReader: ConfigReader[Properties] = cursor =>
    cursor.asObjectCursor
      .map(_.objValue.toConfig.entrySet())
      .map(entries =>
        new Properties {
          entries.forEach(e => Option(e.getValue().unwrapped()).foreach(value => put(e.getKey(), value.toString)))
        }
      )

}
