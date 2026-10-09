package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ContactItem
import com.example.data.model.PhoneNumberItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read app name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Queen Phone", appName)
  }

  @Test
  fun `verify contact model effective photo and primary number`() {
    val contact = ContactItem(
      id = "1",
      lookupKey = "key1",
      displayName = "Grandma Rose",
      devicePhotoUri = "content://contacts/photos/1",
      customPhotoPath = "/data/user/0/com.aistudio.queenphone.rqptk/files/contact_photos/photo_1.jpg",
      phoneNumbers = listOf(
        PhoneNumberItem(number = "555-0199", label = "Mobile", isPrimary = true),
        PhoneNumberItem(number = "555-0100", label = "Home", isPrimary = false)
      ),
      isFavorite = true
    )

    // Custom photo takes precedence over device contact photo
    assertEquals("/data/user/0/com.aistudio.queenphone.rqptk/files/contact_photos/photo_1.jpg", contact.effectivePhotoUri)
    assertEquals("555-0199", contact.primaryNumber)
    assertTrue(contact.isFavorite)
  }
}
