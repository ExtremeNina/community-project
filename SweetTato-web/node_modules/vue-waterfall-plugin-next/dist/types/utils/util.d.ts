export declare const inBrowser: boolean;
export declare const hasIntersectionObserver: boolean;
/**
 * 取值
 * @param {Object | Array} form
 * @param  {...any} selectors
 * @returns
 */
export declare function getValue(form: any, ...selectors: string[]): any[];
/**
 * 防抖
 * @param {*} fn
 * @param {*} delay
 * @returns
 */
export declare function debounce(fn: (args?: any) => void, delay: number): (this: any, ...args: any) => void;
/**
 * 判断是否支持IntersectionObserver
 * @returns {boolean}
 */
export declare function checkIntersectionObserver(): boolean;
/**
 * is object
 *
 * @param {*} val
 * @returns {boolean}
 */
export declare function isObject(val: any): boolean;
/**
 * is primitive
 *
 * @param {*} val
 * @returns {boolean}
 */
export declare function isPrimitive(val: any): boolean;
/**
 * check private key
 *
 * @export
 * @param {*} key
 * @returns {boolean}
 */
export declare function isValidKey(key: any): boolean;
/**
 * Deeply assign the values of all enumerable-own-properties and symbols
 * from one or more source objects to a target object. Returns the target object.
 * https://github.com/jonschlinkert/assign-deep
 *
 * @param {*} target
 * @param {Array} args
 * @returns
 */
export declare function assign(target: any, ...args: any[]): void;
