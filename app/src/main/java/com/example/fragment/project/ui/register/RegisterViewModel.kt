package com.example.fragment.project.ui.register

import androidx.lifecycle.viewModelScope
import com.example.fragment.library.base.http.HttpRequest
import com.example.fragment.library.base.http.post
import com.example.fragment.library.base.vm.BaseViewModel
import com.example.fragment.project.bean.RegisterBean
import com.example.fragment.project.utils.WanHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterState(
    var isLoading: Boolean = false,
    var errorCode: String = "",
    var errorMsg: String = "",
)

class RegisterViewModel : BaseViewModel() {

    private val _uiState = MutableStateFlow(RegisterState())

    val uiState: StateFlow<RegisterState> = _uiState.asStateFlow()

    fun register(username: String, password: String, repassword: String) {
        if (username.isBlank()) {
            _uiState.update {
                it.copy(errorCode = "${System.nanoTime()}", errorMsg = "用户名不能为空")
            }
            return
        }
        if (password.isBlank()) {
            _uiState.update {
                it.copy(errorCode = "${System.nanoTime()}", errorMsg = "密码不能为空")
            }
            return
        }
        if (repassword.isBlank()) {
            _uiState.update {
                it.copy(errorCode = "${System.nanoTime()}", errorMsg = "确认密码不能为空")
            }
            return
        }
        if (password != repassword) {
            _uiState.update {
                it.copy(errorCode = "${System.nanoTime()}", errorMsg = "两次密码不一样")
            }
            return
        }
        _uiState.update {
            it.copy(isLoading = true)
        }
        viewModelScope.launch {
            val request = HttpRequest("user/register")
                    .putParam("username", username)
                    .putParam("password", password)
                    .putParam("repassword", repassword)
            val response = post<RegisterBean>(request)
            WanHelper.setUser(response.data)
            _uiState.update {
                it.copy(isLoading = false, errorCode = response.errorCode, errorMsg = response.errorMsg)
            }
        }
    }

}