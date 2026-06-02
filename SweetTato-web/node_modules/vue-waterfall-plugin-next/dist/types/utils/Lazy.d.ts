import type { CallbackFunction, LazyOptions, ValueFormatterObject } from '../types/lazy';
export default class Lazy {
    lazyActive: boolean;
    crossOrigin: boolean;
    options: LazyOptions;
    _images: WeakMap<object, any>;
    constructor(flag: boolean | undefined, options: LazyOptions, crossOrigin?: boolean);
    config(options?: LazyOptions): void;
    mount(el: HTMLImageElement, binding: string | ValueFormatterObject, callback: CallbackFunction): void;
    resize(el: HTMLImageElement, callback: () => void): void;
    unmount(el: HTMLElement): void;
    /**
     * 设置img的src
     * @param {*} el - img
     * @param {*} src - 原图
     * @param {*} error - 错误图片
     * @param {*} callback - 完成的回调函数，通知组件刷新布局
     * @returns
     */
    _setImageSrc(el: HTMLImageElement, src: string, callback: CallbackFunction, error?: string): void;
    _isOpenLazy(): boolean;
    /**
     * 添加img和对应的observer到weakMap中
     * 开启监听
     * 当出现在可视区域后取消监听
     * @param {*} el - img
     * @param {*} src - 图片
     * @param {*} error - 错误图片
     * @param {*} callback - 完成的回调函数，通知组件刷新布局
     */
    _initIntersectionObserver(el: HTMLImageElement, src: string, callback: CallbackFunction, error?: string): void;
    _valueFormatter(value: ValueFormatterObject | string): ValueFormatterObject;
    _log(callback: () => void): void;
    _realObserver(el: HTMLElement): IntersectionObserver | undefined;
}
