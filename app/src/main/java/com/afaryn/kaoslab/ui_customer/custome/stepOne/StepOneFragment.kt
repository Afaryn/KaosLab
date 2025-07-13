package com.afaryn.kaoslab.ui_customer.custome.stepOne

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.afaryn.kaoslab.R
import com.afaryn.kaoslab.databinding.FragmentStepOneBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StepOneFragment : Fragment() {

    private var _binding: FragmentStepOneBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        setActiveCard(binding.cardTop, binding.textTitleTop)
        loadChildFragment(TopFragment())

        binding.cardTop.setOnClickListener {
            setActiveCard(binding.cardTop, binding.textTitleTop, TopFragment())
        }

        binding.cardBottom.setOnClickListener {
            setActiveCard(binding.cardBottom, binding.textTitleBottom, BottomFragment())
        }

        binding.cardHat.setOnClickListener {
            setActiveCard(binding.cardHat, binding.textTitleHat, HatFragment())
        }
    }

    private fun loadChildFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }

    private fun setActiveCard(card: CardView, textView: TextView, fragment: Fragment) {
        resetAllCards()
        card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.darkBlue))
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.cream))
        loadChildFragment(fragment)
    }

    private fun resetAllCards() {
        resetCard(binding.cardTop, binding.textTitleTop, "Top")
        resetCard(binding.cardBottom, binding.textTitleBottom, "Bottom")
        resetCard(binding.cardHat, binding.textTitleHat, "Hat")
    }

    private fun resetCard(card: CardView, textView: TextView, title: String) {
        card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.darkBlue))
        textView.text = title
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
