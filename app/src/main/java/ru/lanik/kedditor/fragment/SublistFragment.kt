package ru.lanik.kedditor.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import ru.lanik.kedditor.ui.screen.sublist.SublistScreen
import ru.lanik.kedditor.ui.theme.KedditorTheme

class SublistFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                KedditorTheme {
                    SublistScreen()
                }
            }
        }
    }
}