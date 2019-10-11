package no.inspera

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicLong

@RestController
class Controller {

    val counter = AtomicLong()

    @GetMapping("/")
    fun doit(@RequestParam(value = "name", defaultValue = "hoppsann") name: String) =
            mapOf ("Heisann  $name" to counter.incrementAndGet())

}
