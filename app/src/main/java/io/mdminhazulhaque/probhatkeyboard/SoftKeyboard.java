package io.mdminhazulhaque.probhatkeyboard;

import android.annotation.SuppressLint;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class SoftKeyboard extends InputMethodService implements
		KeyboardView.OnKeyboardActionListener {

	private KeyboardView inputView;

	private int lastDisplayWidth;
	private ProbhatKeyboard probhatShiftKeyboard;
	private ProbhatKeyboard probhatBaseKeyboard;
	private ProbhatKeyboard currentKeyboard;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onInitializeInterface() {
		if (probhatBaseKeyboard != null) {
			int displayWidth = getMaxWidth();
			if (displayWidth == lastDisplayWidth)
				return;
			lastDisplayWidth = displayWidth;
		}
		probhatBaseKeyboard = new ProbhatKeyboard(this, R.xml.probhat);
		probhatShiftKeyboard = new ProbhatKeyboard(this, R.xml.probhat_shift);
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateInputView() {
		inputView = (KeyboardView) getLayoutInflater().inflate(R.layout.input,
				null);
		inputView.setOnKeyboardActionListener(this);
		inputView.setKeyboard(probhatBaseKeyboard);
		return inputView;
	}

	@Override
	public void onStartInput(EditorInfo attribute, boolean restarting) {
		super.onStartInput(attribute, restarting);

		switch (attribute.inputType & EditorInfo.TYPE_MASK_CLASS) {
		case EditorInfo.TYPE_CLASS_NUMBER:
		case EditorInfo.TYPE_CLASS_DATETIME:
		case EditorInfo.TYPE_CLASS_PHONE:
		case EditorInfo.TYPE_CLASS_TEXT:
			currentKeyboard = probhatBaseKeyboard;
			updateShiftKeyState(attribute);
			break;

		default:
			currentKeyboard = probhatBaseKeyboard;
			updateShiftKeyState(attribute);
		}

		currentKeyboard.setImeOptions(getResources(), attribute.imeOptions);
	}

	@Override
	public void onFinishInput() {
		super.onFinishInput();

		currentKeyboard = probhatBaseKeyboard;
		if (inputView != null) {
			inputView.closing();
		}
	}

	@Override
	public void onStartInputView(EditorInfo attribute, boolean restarting) {
		super.onStartInputView(attribute, restarting);
		inputView.setKeyboard(currentKeyboard);
		inputView.closing();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (event.getRepeatCount() == 0 && inputView != null) {
				if (inputView.handleBack()) {
					return true;
				}
			}
			break;
		case KeyEvent.KEYCODE_DEL:
			return true;
		case KeyEvent.KEYCODE_ENTER:
			return true;

		default:
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return super.onKeyUp(keyCode, event);
	}

	private void updateShiftKeyState(EditorInfo attr) {
		if (attr != null && inputView != null
				&& probhatBaseKeyboard == inputView.getKeyboard()) {
			int caps = 0;
			EditorInfo ei = getCurrentInputEditorInfo();
			if (ei != null && ei.inputType != EditorInfo.TYPE_NULL) {
				caps = getCurrentInputConnection().getCursorCapsMode(
						attr.inputType);
			}
			inputView.setShifted(caps != 0);
		}
	}

	private void keyDownUp(int keyEventCode) {
		getCurrentInputConnection().sendKeyEvent(
				new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
		getCurrentInputConnection().sendKeyEvent(
				new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
	}

	public void onKey(int primaryCode, int[] keyCodes) {
		if (primaryCode == Keyboard.KEYCODE_DELETE) {
			handleBackspace();
		} else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
			handleShift();
		} else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
			handleClose();
			return;
		} else if (primaryCode == ProbhatKeyboardView.KEYCODE_OPTIONS) {
		} else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE
				&& inputView != null) {
			Keyboard current = inputView.getKeyboard();
			if (current == probhatShiftKeyboard) {
				current = probhatBaseKeyboard;
			} else {
				current = probhatShiftKeyboard;
			}
			inputView.setKeyboard(current);
			if (current == probhatShiftKeyboard) {
				current.setShifted(false);
			}
		} else {
			handleCharacter(primaryCode, keyCodes);
		}
	}

	public void onText(CharSequence text) {
		InputConnection ic = getCurrentInputConnection();
		if (ic == null)
			return;
		ic.beginBatchEdit();
		ic.commitText(text, 0);
		ic.endBatchEdit();
		updateShiftKeyState(getCurrentInputEditorInfo());
	}

	private void handleBackspace() {
		keyDownUp(KeyEvent.KEYCODE_DEL);
		updateShiftKeyState(getCurrentInputEditorInfo());
	}

	private void handleShift() {
		if (inputView == null) {
			return;
		}

		Keyboard currentKeyboard = inputView.getKeyboard();
		if (probhatBaseKeyboard == currentKeyboard) {
			inputView.setShifted(!inputView.isShifted());
		} else if (currentKeyboard == probhatShiftKeyboard) {
			probhatShiftKeyboard.setShifted(true);
		}
	}

	private void handleCharacter(int primaryCode, int[] keyCodes) {

		getCurrentInputConnection().commitText(
				String.valueOf((char) primaryCode), 1);
	}

	private void handleClose() {
		requestHideSelf(0);
		inputView.closing();
	}

	public void swipeRight() {
		keyDownUp(KeyEvent.KEYCODE_SPACE);
	}

	public void swipeLeft() {
		handleBackspace();
	}

	public void swipeDown() {
		handleClose();
	}

	public void swipeUp() {
		
	}

	public void onPress(int primaryCode) {
	}

	public void onRelease(int primaryCode) {
	}
}
