import { requireNativeViewManager } from 'expo-modules-core';
import React, { forwardRef, useImperativeHandle, useRef } from 'react';
import { StyleSheet, ViewStyle } from 'react-native';

import {
  PersianNumberInputHandle,
  PersianNumberInputNativeProps,
  PersianNumberInputProps,
} from './PersianNumberInput.types';

// The native view class itself exposes focus/blur/clear/isFocused as
// AsyncFunctions declared inside the module's View() definition (same
// pattern expo-camera uses for e.g. takePictureAsync) — requireNativeViewManager
// forwards a ref straight to that native instance, so calling them is just
// `nativeRef.current.focus()` etc. once the component has mounted.
type NativeViewInstance = React.Component<PersianNumberInputNativeProps> & {
  focus: () => Promise<void>;
  blur: () => Promise<void>;
  clear: () => Promise<void>;
  isFocused: () => Promise<boolean>;
};

const NativeView: React.ComponentType<
  PersianNumberInputNativeProps & { ref?: React.Ref<NativeViewInstance> }
> = requireNativeViewManager('PersianNumberInput');

// Why the style split exists: RN only auto-forwards a small fixed set of style
// props (backgroundColor, opacity, borderRadius, transform, ...) to ANY
// native view, custom or built-in — that part happens for free. But this
// view renders its own child EditText/UITextField internally, and RN has no
// way to know that "color" or "padding" from JS should end up there instead
// of on the wrapper. RN's own <TextInput> hits the exact same wall — its JS
// wrapper does this same split internally, which is why it feels like
// `style` "just works" for it. We're doing by hand what TextInput.js does
// for you.
const PersianNumberInput = forwardRef<PersianNumberInputHandle, PersianNumberInputProps>(
  function PersianNumberInput(
    { style, onFocus, onBlur, onEndEditing, onKeyPress, onChangeText, ...rest },
    ref
  ) {
    const nativeRef = useRef<NativeViewInstance>(null);

    useImperativeHandle(ref, () => ({
      focus: () => {
        nativeRef.current?.focus();
      },
      blur: () => {
        nativeRef.current?.blur();
      },
      clear: () => {
        nativeRef.current?.clear();
      },
      isFocused: async () => {
        return (await nativeRef.current?.isFocused()) ?? false;
      },
    }));

    const {
      color,
      fontSize,
      fontFamily,
      fontWeight,
      textAlign,
      letterSpacing,
      padding,
      paddingHorizontal,
      paddingLeft,
      paddingRight,
      paddingVertical,
      paddingTop,
      paddingBottom,
      ...layoutStyle
    } = StyleSheet.flatten(style) ?? {};

    // Native only supports one symmetric inset per axis (contentPadding /
    // contentPaddingVertical) — collapse whichever padding form was used down
    // to those two values. Per-side padding (paddingLeft vs paddingRight, or
    // paddingTop vs paddingBottom) isn't supported yet.
    const contentPadding = paddingHorizontal ?? padding ?? paddingLeft ?? paddingRight;
    const contentPaddingVertical = paddingVertical ?? padding ?? paddingTop ?? paddingBottom;

    return (
      <NativeView
        {...rest}
        ref={nativeRef}
        textColor={color as string | undefined}
        fontSize={fontSize as number | undefined}
        fontFamily={fontFamily}
        fontWeight={fontWeight}
        textAlign={textAlign}
        letterSpacing={letterSpacing as number | undefined}
        contentPadding={contentPadding as number | undefined}
        contentPaddingVertical={contentPaddingVertical as number | undefined}
        onChangeText={onChangeText ? (e) => onChangeText(e.nativeEvent.text) : undefined}
        onInputFocus={onFocus}
        onInputBlur={onBlur}
        onEndEditing={onEndEditing ? (e) => onEndEditing(e.nativeEvent.text) : undefined}
        onKeyPress={
          onKeyPress ? (e) => onKeyPress(e.nativeEvent.key as 'Backspace' | 'Enter') : undefined
        }
        style={layoutStyle as ViewStyle}
      />
    );
  }
);

export default PersianNumberInput;
