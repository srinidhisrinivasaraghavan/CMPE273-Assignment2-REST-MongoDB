package main.scala
import javax.persistence.Id
import javax.persistence.GeneratedValue
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
import org.codehaus.jackson.map.annotate.JsonSerialize
import org.codehaus.jackson.annotate.JsonIgnoreProperties
import org.codehaus.jackson.annotate.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.ObjectMapper
import java.sql.Timestamp
import java.util.Date
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar

@Entity
@JsonIgnoreProperties(value =Array({"user_id"}))
class IDCard {
@Id 
@GeneratedValue 
@BeanProperty
var card_id: String = _

var user_id: String = _

@BeanProperty
@NotEmpty
var card_name: String = _
  
@BeanProperty
@NotEmpty
var card_number: String = _

val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z")
val date = new Date()
dateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"))
val PST = dateFormat.format(date)
    

@BeanProperty
@JsonInclude(Include.NON_NULL)
var expiration_date: String = _




  
}