package Commons

import android.util.Log
import com.aarogyaforworkers.awsapi.models.AdminProfile
import com.aarogyaforworkers.awsapi.models.SubUserProfile

class HomePageTags {

    val homeScreen = "HomePage"

    val logoutBtn = "LogoutBtn"

    var adminTag = ""

    val searchView = "userSearchView"

    var savedUserTags = "Ravij"

    fun getUserTag(userProfile: SubUserProfile) : String{
        Log.d("TAG", "getUserTag: ${userProfile.frist_name+userProfile.height}")
        return userProfile.frist_name+userProfile.height
    }

    fun getAdminTag(adminProfile: AdminProfile) : String{
        adminTag = adminProfile.first_name
        return adminTag
    }

    fun getSavedAdminTag() = adminTag

    fun getSavedUserTag() = savedUserTags

    companion object{
        val shared = HomePageTags()
    }
}