package javabot.javadoc

import org.bson.types.ObjectId
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Field
import org.mongodb.morphia.annotations.Id
import org.mongodb.morphia.annotations.Index
import org.mongodb.morphia.annotations.Indexes
import org.mongodb.morphia.annotations.PrePersist
import org.mongodb.morphia.annotations.Reference

@Entity(value = "fields", noClassnameStored = true)
@Indexes(Index(fields = arrayOf(Field("javadocClass"), Field("upperName") )),
      Index(fields = arrayOf(Field("apiId"), Field("javadocClass"), Field("upperName") ))) class JavadocField : JavadocElement {
    @Id
    var id: ObjectId = ObjectId()

    @Reference(lazy = true, idOnly = true)
    lateinit var javadocClass: JavadocClass
    lateinit var name: String
    lateinit var upperName: String
    lateinit var parentClassName: String

    var type: String? = null

    private constructor() {
    }

    constructor(parent: JavadocClass, fieldName: String, fieldType: String) {
        javadocClass = parent
        name = fieldName
        type = fieldType
        apiId = parent.apiId
        val url = parent.directUrl + "#" + name
        longUrl = url
        directUrl = url
        parentClassName = parent.toString()
    }

    @PrePersist fun uppers() {
        upperName = name.toUpperCase()
    }

    override fun toString(): String {
        return "$parentClassName#$name:$type"
    }
}