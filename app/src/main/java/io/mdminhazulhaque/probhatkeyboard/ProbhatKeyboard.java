package io.mdminhazulhaque.probhatkeyboard;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.inputmethodservice.Keyboard;
import android.view.inputmethod.EditorInfo;

public class ProbhatKeyboard extends Keyboard {

	private Keyboard.Key enterKey;

	public ProbhatKeyboard(Context context, int layoutResId) {
		super(context, layoutResId);
	}

	public ProbhatKeyboard(Context context, int layoutTemplateResId,
			CharSequence characters, int columns, int horizontalPadding) {
		super(context, layoutTemplateResId, characters, columns,
				horizontalPadding);
	}

	@Override
	protected Keyboard.Key createKeyFromXml(Resources res, Row parent, int x,
			int y, XmlResourceParser parser) {
		Keyboard.Key key = new Key(res, parent, x, y, parser);
		if (key.codes[0] == 10) {
			enterKey = key;
		}
		return key;
	}

	void setImeOptions(Resources res, int options) {
		if (enterKey == null) {
			return;
		}

		switch (options
				& (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
		case EditorInfo.IME_ACTION_GO:
			enterKey.iconPreview = null;
			enterKey.icon = null;
			enterKey.label = res.getText(R.string.label_go_key);
			break;
		case EditorInfo.IME_ACTION_NEXT:
			enterKey.iconPreview = null;
			enterKey.icon = null;
			enterKey.label = res.getText(R.string.label_next_key);
			break;
		case EditorInfo.IME_ACTION_SEARCH:
			enterKey.icon = null;
			enterKey.label = res.getText(R.string.label_search_key);
			;
			break;
		case EditorInfo.IME_ACTION_SEND:
			enterKey.iconPreview = null;
			enterKey.icon = null;
			enterKey.label = res.getText(R.string.label_send_key);
			break;
		default:
			enterKey.iconPreview = null;
			enterKey.icon = null;
			enterKey.label = null;
			break;
		}
	}

	static class Key extends Keyboard.Key {

		public Key(Resources res, Keyboard.Row parent, int x, int y,
				XmlResourceParser parser) {
			super(res, parent, x, y, parser);
		}

		@Override
		public boolean isInside(int x, int y) {
			return super.isInside(x, codes[0] == KEYCODE_CANCEL ? y - 10 : y);
		}
	}
}
