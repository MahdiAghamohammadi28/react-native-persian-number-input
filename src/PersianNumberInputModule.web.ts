import { registerWebModule, NativeModule } from 'expo';

import { PersianNumberInputModuleEvents } from './PersianNumberInput.types';

class PersianNumberInputModule extends NativeModule<PersianNumberInputModuleEvents> {}

export default registerWebModule(PersianNumberInputModule, 'PersianNumberInputModule');
