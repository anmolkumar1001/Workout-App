package com.anmol.a7minutesworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import com.anmol.a7minutesworkout.databinding.ActivityExerciseBinding

class ExerciseActivity : AppCompatActivity() {
    private var binding : ActivityExerciseBinding? = null

     // Adding a variables for the 10 seconds REST timer
    // Start
    // Variable for Rest Timer and later on we will initialize it.
    private var restTimer: CountDownTimer? = null
    // Variable for timer progress. As initial value the rest progress is set to 0. As we are about to start.
    private var restProgress = 0

    // Adding a variables for the 30 seconds Exercise timer
    // START
    // Variable for Exercise Timer and later on we will initialize it.
    private var exerciseTimer: CountDownTimer? = null
    // Variable for the exercise timer progress. As initial value the exercise progress is set to 0. As we are about to start.
    private var exerciseProgress = 0

    private var exerciseList : ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)

        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        // Initializing and Assigning a default exercise list to our list variable.
        exerciseList = Constants.defaultExerciseList()

        binding?.toolbarExercise?.setNavigationOnClickListener {
            onBackPressed()
        }
        setupRestView()
    }

    //Setting up the Get Ready View with 10 seconds of timer
    //START
    /**
     * Function is used to set the timer for REST.
     */

    // Adding Upcoming exercise label and name
    private fun setupRestView(){
        binding?.flRestView?.visibility = View.VISIBLE
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        binding?.upcomingLabel?.visibility = View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.VISIBLE


        if(restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }

        // Set Upcoming exercise name to the text view
        binding?.tvUpcomingExerciseName?.text = exerciseList!![currentExercisePosition + 1].getName()

        setRestProgressBar()
    }

    private fun setupExerciseView(){

        // Here according to the view make it visible as this is Exercise View so exercise view is visible and rest view is not.
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        // Adding Upcoming exercise name
        binding?.upcomingLabel?.visibility = View.INVISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.INVISIBLE

        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        // Here current exercise name and image is set to exercise view.
        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].getName()

        setExerciseProgressBar()
    }

    // Setting up the 10 seconds timer for rest view and updating it continuously.
    //START
    /**
     * Function is used to set the progress of timer using the progress
     */

    private fun setRestProgressBar(){
        binding?.progressBar?.progress = restProgress

        // Here we have started a timer of 10 seconds so the 10000 is milliseconds is 10 seconds and the countdown interval is 1 second so it 1000.
        restTimer = object: CountDownTimer(10000, 1000){
            override fun onTick(p0: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text = (10 - restProgress).toString()
            }

            override fun onFinish() {

                // Increasing the current position of the exercise after rest view.
                currentExercisePosition++
                setupExerciseView()
            }
        }.start()
    }

    private fun setExerciseProgressBar(){
        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object: CountDownTimer(30000, 1000){
            override fun onTick(p0: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = 30 - exerciseProgress
                binding?.tvTimerExercise?.text = (30 - exerciseProgress).toString()
            }

            override fun onFinish() {

                // Updating the view after completing the 30 seconds exercise.
                if(currentExercisePosition <exerciseList?.size!! -1){
                    setupRestView()
                } else {
                    Toast.makeText(
                        this@ExerciseActivity,
                        "Congratulations!👏 You have completed the 7 minutes workout.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()

        if(restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }

        if(exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        binding = null
    }
}