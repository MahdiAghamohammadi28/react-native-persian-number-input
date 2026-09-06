import { StyleProp, TextStyle, ViewStyle } from 'react-native';

// The style shape consumers write — same idea as RN's own <TextInput>: layout
// props (width, margin, flex, borderWidth, ...) AND content props (color,
// fontSize, fontFamily, fontWeight, letterSpacing, textAlign, padding) live in
// one `style`, just like any other RN component. `PersianNumberInputView.tsx`
// splits this at the JS boundary before it reaches native (see the comment
// there for why that split has to happen at all).
//
// `textAlign` is narrowed to the 3 values the native side understands — RN's
// own TextStyle also allows "auto" / "justify" / "justify-all", which this
// native input doesn't support.
// `fontWeight` is narrowed to "normal" | "bold" — native fonts are resolved by
// exact registered family name (e.g. "IranYekan-Bold"), not by numeric weight
// variants of a single family, so RN's full fontWeight range ("100"..."900")
// wouldn't map to anything real here.
export type PersianNumberInputStyle = Omit<ViewStyle & TextStyle, 'textAlign' | 'fontWeight'> & {
  textAlign?: 'left' | 'right' | 'center';
  fontWeight?: 'normal' | 'bold';
};

export type PersianNumberInputProps = {
  value?: string;
  placeholder?: string;
  rtl?: boolean;
  style?: StyleProp<PersianNumberInputStyle>;

  // Behavior props — same names/values as RN's <TextInput>, kept deliberately
  // narrower than RN's full set to just what's implemented natively so far.
  // This component is numeric-only, so multiline/autoCapitalize/autoCorrect
  // etc. are intentionally not part of the surface.
  editable?: boolean;
  maxLength?: number;
  autoFocus?: boolean;
  selectTextOnFocus?: boolean;
  allowFontScaling?: boolean;
  keyboardType?: 'default' | 'numeric' | 'phone-pad' | 'email-address';
  returnKeyType?: 'default' | 'done' | 'next' | 'search' | 'send';
  placeholderTextColor?: string;
  selectionColor?: string;
  cursorColor?: string;
  caretHidden?: boolean;
  secureTextEntry?: boolean;
  // "sms-otp" wires up the platform's real one-time-code autofill mechanism
  // (Android SMS Retriever autofill hint / iOS textContentType(.oneTimeCode)).
  autoComplete?: 'off' | 'sms-otp';

  onChangeText?: (text: string) => void;
  onFocus?: () => void;
  onBlur?: () => void;
  onSubmitEditing?: () => void;
  onEndEditing?: (text: string) => void;
  // Currently reports "Backspace" and "Enter" only — matches the subset RN's
  // own onKeyPress reliably delivers cross-platform for a numeric keyboard.
  onKeyPress?: (key: 'Backspace' | 'Enter') => void;
};

// Imperative methods exposed via ref — mirrors RN's own <TextInput> ref API
// (`inputRef.current?.focus()`, etc.) so this drops in as a familiar
// replacement.
export type PersianNumberInputHandle = {
  focus: () => void;
  blur: () => void;
  clear: () => void;
  isFocused: () => Promise<boolean>;
};

// What actually crosses the JS -> native bridge, after PersianNumberInputView.tsx
// has pulled the content-related style keys out into their own props (native
// custom views only get automatic style handling for basic layout/paint
// props — anything destined for a child view like our EditText/UITextField
// needs an explicit prop; see PersianNumberInputView.tsx).
//
// onInputFocus/onInputBlur (not onFocus/onBlur!): RN reserves "topFocus" and
// "topBlur" globally as bubbling events across every native component. Expo
// Modules' Events() registers custom events as direct (non-bubbling) by
// default, so naming ours "onFocus"/"onBlur" collides with that global
// registration and crashes with "Event cannot be both direct and bubbling:
// topFocus" the moment this view mounts. Renaming the native-facing event
// sidesteps the collision; PersianNumberInputView.tsx still exposes the
// familiar onFocus/onBlur names to consumers.
//
// textColor (not color!): "color" is a reserved prop name in RN's common-props
// layer, which assumes it's a numeric packed color rather than the String
// Expo's Prop declares here — crashes with "cannot be cast from String to
// double" if named "color". Renaming sidesteps that collision too.
export type PersianNumberInputNativeProps = {
  value?: string;
  placeholder?: string;
  fontSize?: number;
  allowFontScaling?: boolean;
  fontFamily?: string;
  fontWeight?: 'normal' | 'bold';
  textColor?: string;
  placeholderTextColor?: string;
  selectionColor?: string;
  cursorColor?: string;
  caretHidden?: boolean;
  textAlign?: 'left' | 'right' | 'center';
  letterSpacing?: number;
  rtl?: boolean;
  contentPadding?: number;
  contentPaddingVertical?: number;
  editable?: boolean;
  secureTextEntry?: boolean;
  autoComplete?: 'off' | 'sms-otp';
  maxLength?: number;
  autoFocus?: boolean;
  selectTextOnFocus?: boolean;
  keyboardType?: 'default' | 'numeric' | 'phone-pad' | 'email-address';
  returnKeyType?: 'default' | 'done' | 'next' | 'search' | 'send';
  style?: ViewStyle;
  onChangeText?: (e: { nativeEvent: { text: string } }) => void;
  onInputFocus?: () => void;
  onInputBlur?: () => void;
  onSubmitEditing?: () => void;
  onEndEditing?: (e: { nativeEvent: { text: string } }) => void;
  onKeyPress?: (e: { nativeEvent: { key: string } }) => void;
};
