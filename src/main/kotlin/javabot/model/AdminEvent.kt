package javabot.model

import javabot.Javabot
import org.bson.types.ObjectId
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Field
import org.mongodb.morphia.annotations.Id
import org.mongodb.morphia.annotations.Index
import org.mongodb.morphia.annotations.Indexed
import org.mongodb.morphia.annotations.Indexes
import org.mongodb.morphia.annotations.Transient
import java.io.Serializable
import java.time.LocalDateTime
import javax.inject.Inject

@Entity("events")
@Indexes(Index(fields = arrayOf(Field("state"), Field("requestedOn"))))
open class AdminEvent : Serializable, Persistent {

    @Inject
    @Transient
    lateinit var bot: Javabot

    @Id
    var id: ObjectId = ObjectId()

    @Indexed(expireAfterSeconds = 60 * 60 * 24)
    var completed: LocalDateTime? = null

    var state: State = State.NEW

    lateinit var requestedBy: String
    lateinit var requestedOn: LocalDateTime
    lateinit var type: EventType

    protected constructor()

    constructor(requestedBy: String, type: EventType, requestedOn: LocalDateTime = LocalDateTime.now()) : this() {
        this.requestedBy = requestedBy
        this.requestedOn = requestedOn
        this.type = type
    }

    fun handle() {
        when (type) {
            EventType.ADD -> add()
            EventType.DELETE -> delete()
            EventType.UPDATE -> update()
            EventType.RELOAD -> reload()
        }
    }

    override fun toString(): String {
        return "AdminEvent{id=${id}, requestedOn=${requestedOn}, completed=${completed}, state=${state}, type=${type}}"
    }

    open fun add() {
    }

    open fun delete() {
    }

    open fun update() {
    }

    open fun reload() {
    }
}

enum class State {
    NEW,
    PROCESSING,
    COMPLETED,
    FAILED
}
