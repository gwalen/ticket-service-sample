package eventworld.context.event.repository

import eventworld.common.database.BaseDb.driver.api._
import eventworld.context.event.domain.Event
import slick.lifted.TableQuery

class EventRepository() {

  private val events = TableQuery[Events]

  def insert(event: Event): DBIOAction[Int, NoStream, Effect.Write] =
    events += event
}
