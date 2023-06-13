package com.anmol.a7minutesworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.anmol.a7minutesworkout.databinding.ActivityExerciseBinding
import com.anmol.a7minutesworkout.databinding.DialogCustomBackConfirmationBinding
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding : ActivityExerciseBinding? = null

     // Adding a variables for the 10 seconds REST timer
    // Start
    // Variable for Rest Timer and later on we will initialize it.
    private var restTimer: CountDownTimer? = null
    // Variable for timer progress. As initial value the rest progress is set to 0. As we are about to start.
    private var restProgress = 0
    private var restTimerDuration: Long = 1

    // Adding a variables for the 30 seconds Exercise timer
    // START
    // Variable for Exercise Timer and later on we will initialize it.
    private var exerciseTimer: CountDownTimer? = null
    // Variable for the exercise timer progress. As initial value the exercise progress is set to 0. As we are about to start.
    private var exerciseProgress = 0

    private var exerciseTimerDuration: Long = 1

    private var exerciseList : ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var tts: TextToSpeech? = null

    // Declaring the variable of the media player for playing a notification sound when the exercise is about to start.
    private var player: MediaPlayer? = null

    // Declaring an exerciseAdapter object which will be initialized later.
    private var exerciseAdapter: ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)

        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarExercise?.setNavigationOnClickListener {
            //Calling the function of custom dialog for back button confirmation
            customDialogForBackButton()
        }

        tts = TextToSpeech(this, this)
        // Initializing and Assigning a default exercise list to our list variable.
        exerciseList = Constants.defaultExerciseList()



        setupRestView()
        // setting up the exercise recycler view
        setupExerciseStatusRecyclerView()
    }

    override fun onBackPressed() {
        customDialogForBackButton()
        //super.onBackPressed()
    }
    //Performing the steps to show the custom dialog for back button confirmation while the exercise is going on.
    private fun customDialogForBackButton(){
        val customDialog = Dialog(this);
        //create a binding variable
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        //The resource will be inflated, adding all top-level views to the screen.
        customDialog.setContentView(dialogBinding.root)
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.btnYes.setOnClickListener {
           this@ExerciseActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    //Setting up the Get Ready View with 10 seconds of timer
    //START
    /**
     * Function is used to set the timer for REST.
     */

    // Adding Upcoming exercise label and name
    private fun setupRestView(){

        // Playing a notification sound when the exercise is about to start when you are in the rest state
        // the sound file is added in the raw folder as resource.
        try{
            val soundURI = Uri.parse(
                "android.resource://com.anmol.a7minutesworkout/" + R.raw.press_start)
            player = MediaPlayer.create(applicationContext, soundURI)

        // Sets the player to be looping or non-looping.
            player?.isLooping = false
            player?.start()

        }catch (e: Exception){
            e.printStackTrace()
        }


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
        speakOut(exerciseList!![currentExercisePosition].getName())

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
        restTimer = object: CountDownTimer(restTimerDuration*1000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                restProgress++
                binding?.progressBar?.progress = 10 - restProgress
                binding?.tvTimer?.text = (10 - restProgress).toString()
            }

            override fun onFinish() {

                // Increasing the current position of the exercise after rest view.
                currentExercisePosition++

                // When we are getting an updated position of exercise set that item in the list as selected and notify the adapter class.
                // START
                exerciseList!![currentExercisePosition].setIsSelected(true) // Current Item is selected
                exerciseAdapter!!.notifyDataSetChanged() // Notified the current item to adapter class to reflect it into UI.
                // END
                setupExerciseView()
            }
        }.start()
    }

    private fun setExerciseProgressBar(){
        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object: CountDownTimer(exerciseTimerDuration*1000, 1000){
            override fun onTick(millisUntilFinished: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = 30 - exerciseProgress
                binding?.tvTimerExercise?.text = (30 - exerciseProgress).toString()
            }

            override fun onFinish() {

                // Updating the view after completing the 30 seconds exercise.
                if(currentExercisePosition < exerciseList?.size!! - 1){
                    // We have changed the status of the selected item and updated the status of that, so that the position is set as completed in the exercise list.
                    // START
                    exerciseList!![currentExercisePosition].setIsSelected(false) // exercise is completed so selection is set to false
                    exerciseList!![currentExercisePosition].setIsCompleted(true) // updating in the list that this exercise is completed
                    exerciseAdapter!!.notifyDataSetChanged() // Notifying the adapter class.
                    // END
                    setupRestView()
                } else {
                    finish()
                    val intent = Intent(this@ExerciseActivity,FinishActivity::class.java)
                    startActivity(intent)
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

        if(tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }

       // When the activity is destroyed if the media player instance is not null then stop it.
        if(player != null){
            player!!.stop()
        }

        super.onDestroy()
        binding = null
    }

//    Called to signal the completion of the TextToSpeech engine initialization.
    override fun onInit(status: Int) {

    if(status == TextToSpeech.SUCCESS) {
        // set US English as language for tts
        val result = tts?.setLanguage(Locale.US)

        if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("TTS", "The Language specified is not supported!")
        }

        } else {
            Log.e("TTS", "Initialization Failed!")
        }
    }

    // Function is used to speak the text that we pass to it.
    private fun speakOut(text: String) {
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    // Binding adapter class to recycler view and setting the recycler view layout manager and passing a list to the adapter.
    // START
    private fun setupExerciseStatusRecyclerView() {

        // Defining a layout manager for the recycle view
        // Here we have used a LinearLayout Manager with horizontal scroll.
        binding?.rvExerciseStatus?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // As the adapter expects the exercises list and context so initialize it passing it.
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)

        // Adapter class is attached to recycler view
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
    }
    // END
}