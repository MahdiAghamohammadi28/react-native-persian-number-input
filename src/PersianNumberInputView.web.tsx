import * as React from 'react';

import { PersianNumberInputViewProps } from './PersianNumberInput.types';

export default function PersianNumberInputView(_props: PersianNumberInputViewProps) {
  return (
    <div
      style={{
        backgroundColor: '#aabbcc',
        flex: 1,
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
      }}>
      <span>PersianNumberInput - native view</span>
    </div>
  );
}
