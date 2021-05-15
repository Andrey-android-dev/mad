package ru.skillbranch.skillarticles.viewmodels

import android.os.Bundle
import android.util.Log
import androidx.annotation.UiThread
import androidx.annotation.VisibleForTesting
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import java.io.Serializable

/**
 * Type description here....
 *
 * Created by Andrey on 03.04.2021
 */
abstract class BaseViewModel<T>(initState: T, private val savedStateHandle: SavedStateHandle) :
    ViewModel() where T: VMState{

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    val notifications = MutableLiveData<Event<Notify>>()

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    val state: MediatorLiveData<T> = MediatorLiveData<T>().apply {
        val restoredState = savedStateHandle.get<Any>("state")?.let{
            if(it is Bundle) initState.fromBundle(it) as? T
            else it as T
        }
        Log.d("BaseViewModel","handle restore state $restoredState")
        value = restoredState ?: initState

    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    val currentState
        get() = state.value!!


    @UiThread
    protected inline fun updateState(update: (currentState: T) -> T) {
        val updatedState: T = update(currentState)
        state.value = updatedState
    }

    @UiThread
    protected fun notify(content: Notify) {
        notifications.value = Event(content)
    }

    fun observeState(owner: LifecycleOwner, onChanged: (newState: T) -> Unit) {
        state.observe(owner, Observer { onChanged(it!!) })
    }

    fun <D> observeSubState(
        owner: LifecycleOwner,
        transform: (T) -> D,
        onChanged: (substate: D) -> Unit
    ) {
        state
            .map(transform)
            .distinctUntilChanged()
            .observe(owner, Observer { onChanged(it!!) })
    }

    fun observeNotifications(owner: LifecycleOwner, onNotify: (notification: Notify) -> Unit) {
        notifications.observe(owner, EventObserver { onNotify(it) })
    }

    protected fun <S> subscribeOnDataSource(
        source: LiveData<S>,
        onChanged: (newValue: S, currentState: T) -> T?
    ) {
        state.addSource(source) {
            state.value = onChanged(it, currentState) ?: return@addSource
        }
    }

    fun saveState() {
        Log.d("TEST123", "save state $currentState")
        savedStateHandle.set("state", currentState)
    }

//    fun restoreState() {
//        val restoredState = savedStateHandle.get<T>("state")
//        Log.d("TEST123", "restore sate $restoredState")
//        restoredState ?: return
//        state.value = restoredState
//    }

}

class ViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val params: String
) : AbstractSavedStateViewModelFactory(owner, bundleOf()) {

    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
            return ArticleViewModel(params, handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class Event<out E>(private val content: E) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): E? {
        return if (hasBeenHandled) null
        else {
            hasBeenHandled = true
            content
        }
    }

    fun peekContent(): E = content


}

class EventObserver<E>(private val onEventUnhandledContent: (E) -> Unit) : Observer<Event<E>> {
    override fun onChanged(event: Event<E>?) {
        event?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}

sealed class Notify(val message: String) {
    data class TextMessage(val msg: String) : Notify(msg)

    data class ActionMessage(
        val msg: String,
        val actionLabel: String,
        val actionHandler: (() -> Unit)
    ) : Notify(msg)

    data class ErrorMessage(
        val msg: String,
        val errLabel: String,
        val errHandler: (() -> Unit)?
    ) : Notify(msg)
}

public interface VMState : Serializable {
    fun toBundle(): Bundle
    fun fromBundle(bundle: Bundle): VMState?
}