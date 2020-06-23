package eventworld.context.event.domain

case class Event(id: Long, name: String, ticketCount: Long, maxTicketsForClient: Int)
