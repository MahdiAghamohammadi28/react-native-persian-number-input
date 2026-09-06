import { requireNativeView } from 'expo';
import * as React from 'react';

import { PersianNumberInputViewProps } from './PersianNumberInput.types';

const NativeView: React.ComponentType<PersianNumberInputViewProps> = requireNativeView('PersianNumberInput');

export default function PersianNumberInputView(props: PersianNumberInputViewProps) {
  return <NativeView {...props} />;
}
