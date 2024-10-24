package dev.braian.habitsre.domain.model

import com.google.firebase.database.Exclude
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

data class Habit(
    var id: String?,
    var name: String?,
    var backgroundColor: String?,
    var difficulty: Difficulty? = Difficulty.Easy,
    var userId: String? = null,
    var createdAt: Long?,

) {
    var timeDecreased: Long? = 0
    var timeDifferenceInMinutes: Long? = 0

    constructor() : this(null, "", "", Difficulty.Easy, "", 0)

    fun getTimeLimit(difficulty: Difficulty): Long {
        return when (difficulty) {
            Difficulty.Easy -> 10
            Difficulty.Normal -> 20
            Difficulty.Hard -> 60
            Difficulty.Hardest -> 2 * 60
        }
    }

    fun addDecreaseTime(difficulty: Difficulty?):Boolean {
        return if( Difficulty.Easy.timeInMinutes > this.getTImeDifferenceInMinutes()) {
            false
        } else {
            this.timeDecreased = this.timeDecreased?.plus(difficulty!!.timeInMinutes)
            true
        }
    }

    @Exclude
    fun getTImeDifferenceInMinutes(): Long {
        val createdAt = LocalDateTime.ofInstant(Instant.ofEpochSecond(createdAt!!), ZoneOffset.UTC)
        val now = LocalDateTime.now(ZoneId.systemDefault())

        var timeDifference  = ChronoUnit.MINUTES.between(createdAt, now)

        if( timeDecreased != null){
            timeDifference -= timeDecreased!!
        }

        if(timeDifference < 0) {
            timeDifference = 0
        }
        return timeDifference
    }

    fun saveHabit(
        name: String?,
        backgroundColor: String?,
        difficulty: Difficulty?,
        userId: String?,
        createdAt: Long? = null
    ) {
        this.name = name
        this.backgroundColor = backgroundColor
        this.difficulty = difficulty
        this.userId = userId
        this.createdAt = createdAt
    }

    constructor(
        id: String?,
        name: String?,
        backgroundColor: String?,
        difficulty: Difficulty?,
        userId: String?
    ) : this(id, name, backgroundColor, difficulty, userId, System.currentTimeMillis())

    fun calculateTimeComponents(timeDifference: Long): Triple<Long, Long, Long> {
        val days = timeDifference / (24 * 60) // 1 dia = 24 horas * 60 minutos
        val hours = (timeDifference % (24 * 60)) / 60
        val minutes = timeDifference % 60
        return Triple(days, hours, minutes)
    }
}

enum class Difficulty(val description: String, val timeInMinutes: Int) {
    Easy("Quick and straightforward, ideal for a fast start.", 10),
    Normal("Balanced pace, with a moderate time to complete.", 20),
    Hard("Tough and time-consuming, for those up for a challenge.", 30),
    Hardest("Intense and prolonged, designed for the most dedicated.", 60)
}