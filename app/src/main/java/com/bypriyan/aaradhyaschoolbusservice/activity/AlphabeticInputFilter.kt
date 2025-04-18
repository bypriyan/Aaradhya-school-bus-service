import android.text.InputFilter
import android.text.Spanned

class AlphabeticInputFilter : InputFilter {
    override fun filter(
        source: CharSequence, // The new text being entered
        start: Int, // Start index of the new text
        end: Int, // End index of the new text
        dest: Spanned, // The current text in the field
        dstart: Int, // Start index of the current text
        dend: Int // End index of the current text
    ): CharSequence? {
        // Regex to allow only alphabetic characters and spaces
        val regex = Regex("[^A-Za-z ]")
        val filtered = source.toString().replace(regex, "")
        return if (filtered == source) {
            null // Accept the input
        } else {
            filtered // Return the filtered input
        }
    }
}