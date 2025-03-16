package ru.hse.crowns.ui.killerSudokuHome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ru.hse.crowns.databinding.FragmentKillerSudokuHomeBinding

class KillerSudokuFragment : Fragment() {

    private var _binding: FragmentKillerSudokuHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val killerSudokuViewModel =
            ViewModelProvider(this).get(KillerSudokuViewModel::class.java)

        _binding = FragmentKillerSudokuHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        killerSudokuViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}