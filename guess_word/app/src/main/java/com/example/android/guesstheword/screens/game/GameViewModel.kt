package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class GameViewModel (): ViewModel() {
    // current word
    private val _word = MutableLiveData<String>()
    val word: LiveData<String>
        get() = _word
    // current score
    private val _score = MutableLiveData<Int> ()
    val score : LiveData<Int>
        get() = _score
    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish : LiveData<Boolean>
        get() = _eventGameFinish
    private val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
        get() = _currentTime
    val currentTimeString: LiveData<String>
        get() = Transformations.map(currentTime) {time ->
            DateUtils.formatElapsedTime(time)
        }
    val currentWordHintString: LiveData<String>
        get() = Transformations.map(word) {word ->
            val randomPosition = (1 until word.length).random()
            "Current word has ${word.length} letters \n" +
                    "The letter at position $randomPosition " +
                    "is ${word[randomPosition-1].toUpperCase()}"
        }

    private lateinit var wordList: MutableList<String>
    private val timer: CountDownTimer

    init {
        _word.value = ""
        _score.value = 0

        timer = object: CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = millisUntilFinished/ONE_SECOND
            }

            override fun onFinish() {
                _currentTime.value = DONE
                onGameFinish()
            }
        }
        timer.start()

        resetList()
        nextWord()

        Log.i(TAG, "GameViewModel is created")

    }

    override fun onCleared() {
        super.onCleared()

        timer.cancel()

        Log.i(TAG, "GameViewModel is destroy")
    }

    private fun resetList() {
        wordList = mutableListOf(
                "queen",
                "hospital",
                "basketball",
                "cat",
                "change",
                "snail",
                "soup",
                "calendar",
                "sad",
                "desk",
                "guitar",
                "home",
                "railway",
                "zebra",
                "jelly",
                "car",
                "crow",
                "trade",
                "bag",
                "roll",
                "bubble"
        )
        wordList.shuffle()
    }

    private fun nextWord() {
        if (wordList.isNotEmpty()) {
            //Select and remove a word from the list
            _word.value = wordList.removeAt(0)
        }else {
            resetList()
        }
    }

    fun onGameFinish() {
        _eventGameFinish.value = true
    }

    fun onGameFinishComplete() {
        _eventGameFinish.value = false
    }

    fun onSkip() {
        _score.value = (score.value)?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        _score.value = (score.value)?.plus(1)
        nextWord()
    }

    companion object {
        const val TAG = "GameViewModel"

        private const val DONE = 0L

        private const val ONE_SECOND = 1000L

        private const val COUNTDOWN_TIME = 6000L

    }
}