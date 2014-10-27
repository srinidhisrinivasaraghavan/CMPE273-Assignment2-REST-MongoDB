package main.scala

import java.lang.Long
import javax.persistence.Entity
import scala.beans.BeanProperty
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.boot._
import org.springframework.boot.autoconfigure._
import org.springframework.stereotype._
import org.springframework.web.bind.annotation._
import java.util._
import scala.collection.JavaConversions._
import java.sql.Timestamp
import java.util.Date
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import org.codehaus.jackson.map.annotate.JsonSerialize
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "idCards")
@Entity
class IDCard {

@Id 
@BeanProperty
var card_id: String = _

var user_id: String = _

@BeanProperty
@NotEmpty
var card_name: String = _
  
@BeanProperty
@NotEmpty
var card_number: String = _

@BeanProperty
@JsonInclude(Include.NON_NULL)
var expiration_date: String = _




  
}