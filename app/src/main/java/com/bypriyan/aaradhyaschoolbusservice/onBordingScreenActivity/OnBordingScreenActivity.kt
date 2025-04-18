package com.bypriyan.aaradhyaschoolbusservice.onBordingScreenActivity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.viewpager2.widget.ViewPager2
import com.bypriyan.aaradhyaschoolbusservice.activity.CheckOut1
import com.bypriyan.aaradhyaschoolbusservice.activity.DashBoard1Activity
import com.bypriyan.aaradhyaschoolbusservice.activity.LoginActivity
import com.bypriyan.aaradhyaschoolbusservice.activity.SlabActivity
import com.bypriyan.aaradhyaschoolbusservice.databinding.ActivityOnBordingScreenBinding
import com.bypriyan.bustrackingsystem.utility.Constants
import com.bypriyan.bustrackingsystem.utility.PreferenceManager
import com.bypriyan.sharemarketcourseinhindi.adapter.AdapterOnBordingScreen
import com.bypriyan.sharemarketcourseinhindi.model.ModelOnBordingScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class OnBordingScreenActivity : AppCompatActivity() {

    private var backPressedTime: Long = 0
    private val backPressThreshold: Long = 2000 // 2 seconds

    lateinit var binding: ActivityOnBordingScreenBinding
    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityOnBordingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set adapter to ViewPager2
        val adapter = AdapterOnBordingScreen(this, getListOfOnBordingScreenContent())
        binding.viewPager2.adapter = adapter

        binding.wormDotsIndicator.attachTo(binding.viewPager2)

        binding.viewPager2.isUserInputEnabled = false

        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                when (position) {
                    0 -> {
                        binding.previousBtn.visibility = View.GONE
                        binding.nextBtn.visibility = View.VISIBLE
                        binding.nextBtn.text = "NEXT"
                    }
                    1 -> {
                        binding.previousBtn.visibility = View.VISIBLE
                        binding.nextBtn.visibility = View.VISIBLE
                        binding.nextBtn.text = "NEXT"
                    }
                    2 -> {
                        binding.previousBtn.visibility = View.VISIBLE
                        binding.nextBtn.text = "Continue"
                    }
                    else -> binding.previousBtn.visibility = View.GONE
                }
            }
        })

        binding.nextBtn.setOnClickListener {
            val nextIndex = binding.viewPager2.currentItem + 1
            if (nextIndex < adapter.itemCount) {
                binding.viewPager2.currentItem = nextIndex
            } else {
                // Save preference BEFORE launching login
                preferenceManager.putBoolean(Constants.KEY_IS_ONBORDING_SCREEN_SEEN, true)
                startActivity(Intent(this, SlabActivity::class.java))
                finish()
            }
        }

        binding.previousBtn.setOnClickListener {
            val previousIndex = binding.viewPager2.currentItem - 1
            if (previousIndex >= 0) {
                binding.viewPager2.currentItem = previousIndex
            }
        }
    }

    override fun onBackPressed() {
        if (backPressedTime + backPressThreshold > System.currentTimeMillis()) {
            super.onBackPressed()
            finish()
        } else {
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }

    fun getListOfOnBordingScreenContent(): List<ModelOnBordingScreen> {
        return listOf(
            ModelOnBordingScreen(
                "https://banner2.cleanpng.com/20180325/vow/av067p996.webp",
                "Seamless School Transportation",
                "Easily manage your child’s school transportation with secure booking!"
            ),
            ModelOnBordingScreen(
                "https://img.freepik.com/premium-vector/secure-payment-with-mobile-banking-application_258153-435.jpg?semt=ais_hybrid",
                "Safe & Secure Payments",
                "Make hassle-free payments using Razorpay with options for one-time or installment plans."
            ),
            ModelOnBordingScreen(
                "https://img.freepik.com/premium-vector/grab-this-amazing-flat-illustration-notification_9206-2839.jpg?semt=ais_hybrid",
                "Stay Notified & Informed",
                "Get real-time notifications for installment reminders, trip updates, and important school alerts."
            )
        )
    }

    override fun onStart() {
        super.onStart()
        // Ensure PreferenceManager is initialized before using it
        if (!::preferenceManager.isInitialized) {
            preferenceManager = PreferenceManager(this)
        }
        // Check if the onboarding screen was seen
        val hasSeenOnboarding = preferenceManager.getBoolean(Constants.KEY_IS_ONBORDING_SCREEN_SEEN)

        if (hasSeenOnboarding) {
            // If onboarding is already completed, go to LoginActivity
            if (preferenceManager.getBoolean(Constants.KEY_IS_LOGGED_IN)) {
                if (preferenceManager.getBoolean(Constants.PAYMENT_STATUS)) {
                    startActivity(Intent(this, DashBoard1Activity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, CheckOut1::class.java))
                    finish()
                }
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}