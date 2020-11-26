package com.ruxbit.instagrab

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import kotlinx.coroutines.*

class MyAccessibilityService : AccessibilityService() {
    private var lastClickMillis = 0L
    private var coroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d(TAG, "Service connected")
    }

    private fun findProfileNode(curr: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (curr.contentDescription == "Profile")
            return curr
        for (i in 0 until curr.childCount) {
            findProfileNode(curr.getChild(i)).also {
                if (it != null)
                    return it
            }
        }
        return null
    }

    private fun getNodeByPath(path: List<Int>): AccessibilityNodeInfo? {
        var node = rootInActiveWindow
        path.forEach { if (node.childCount > it) node = node.getChild(it) else return null }
        return node
    }

    private fun clickProfile() {
        findProfileNode(rootInActiveWindow)?.performAction(AccessibilityNodeInfo.ACTION_CLICK)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d(TAG, "Event")
        if (System.currentTimeMillis() - lastClickMillis > 3000) {
            clickProfile()
            lastClickMillis = System.currentTimeMillis()
        }
        val login = getNodeByPath(NICKNAME_PATH)?.text?.toString()
        val nameSurname = getNodeByPath(NAME_SURNAME_PATH)?.text?.toString()
        if (login?.isNotBlank() == true && nameSurname?.isNotBlank() == true) {
            Log.d(TAG, "Login = $login, Name Surname = $nameSurname")
            lastClickMillis = System.currentTimeMillis() + 10000

            coroutineScope.launch {
                AppDatabase.getInstance().userDao().insert(User(login, nameSurname))
            }
        }
    }

    override fun onInterrupt() {
        Log.d(TAG, "Service interrupted")
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        Log.d(TAG, "Service destroyed")
    }

    companion object {
        private const val TAG = "instagrabb"
        private val NICKNAME_PATH = listOf(1)
        private val NAME_SURNAME_PATH = listOf(0, 1, 0, 4)
        private val PROFILE_BTN_PATH = listOf(8)
    }

//
//    С помощью функций ниже можно узнать путь к нужным textview в layout'е
//
//    private fun findNodeByText(str: String, curr: AccessibilityNodeInfo): Boolean {
//        if (curr.text == str)
//            return true
//        for (i in 0 until curr.childCount) {
//            if (findNodeByText(str, curr.getChild(i))) {
//                Log.d(TAG, i.toString())
//                return true
//            }
//        }
//        return false
//    }
//
//    private fun findNodeByContentDesc(desc: String, curr: AccessibilityNodeInfo): Boolean {
//        if (curr.contentDescription == desc)
//            return true
//        for (i in 0 until curr.childCount) {
//            if (findNodeByContentDesc(desc, curr.getChild(i))) {
//                Log.d(TAG, i.toString())
//                return true
//            }
//        }
//        return false
//    }
//
//    private fun findNodes() {
//        findNodeByText("xbit1234", rootInActiveWindow)
//        Log.d(TAG, "********")
//        findNodeByText("Руслан Ямашев", rootInActiveWindow)
//        Log.d(TAG, "********")
//        findNodeByContentDesc("Profile", rootInActiveWindow)
//    }
}