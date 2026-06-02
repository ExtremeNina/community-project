/**
 * @category interactive
 */
export type WritableElement = HTMLInputElement | HTMLTextAreaElement;
/**
 * @category interactive
 */
export type CopyableElement = HTMLElement | WritableElement;
/**
 * @category interactive
 */
export interface ClipboardInstance {
	/**
	 * Determine whether the clipboard is supported
	 */
	isSupported: boolean;
	/**
	 * Copy the text passed in or the text of the specified DOM element
	 *
	 * @example
	 *
	 * ```
	 * clipboard.copy(document.querySelector('.foo'))
	 * ```
	 */
	copy: (el: CopyableElement) => Promise<boolean>;
	/**
	 * Cut the text passed in or the text of the specified DOM element
	 *
	 * @example
	 *
	 * ```
	 * clipboard.cut(document.querySelector('.foo'))
	 * ```
	 */
	cut: (el: WritableElement) => Promise<boolean>;
	/**
	 * Read the text content of the clipboard
	 */
	read: () => Promise<string>;
	/**
	 * Write text content to clipboard
	 */
	write: (text: string) => Promise<boolean>;
}
/**
 * Extensions based on the Clipboard API
 *
 * @category interactive
 */
export declare const clipboard: ClipboardInstance;
/**
 * The actual type of the data
 *
 * @category data
 */
export type DataType = "boolean" | "string" | "number" | "bigint" | "symbol" | "null" | "undefined" | "function" | "object" | "array" | "date" | "error" | "set" | "map" | "weakSet" | "weakMap" | "file" | "blob" | "arraybuffer" | "regexp";
/**
 * Get the real data type
 *
 * @category data
 */
export declare function getDataType(target: any): DataType;
/**
 * Determine whether the data is an object
 *
 * @category data
 */
export declare function isObject(target: any): boolean;
/**
 * Determine whether the specified key exists on the object
 *
 * @category data
 */
export declare function hasKey(obj: Record<string, any>, key: string): boolean;
/**
 * @category env
 */
export declare const isBrowser: boolean;
/**
 * @category env
 */
export declare const isServer: boolean;
/**
 * Current user agent
 *
 * @category env
 */
export declare const ua: string;
/**
 * @category env
 */
export declare const mobileDevicesRegExp: RegExp;
/**
 * @category env
 */
export declare const isMobile: boolean;
/**
 * @category env
 */
export declare const isDesktop: boolean;
/**
 * @category env
 */
export declare const isAndroid: boolean;
/**
 * @category env
 */
export declare const isIOS: boolean;
/**
 * @category env
 */
export declare const isUniApp: boolean;
/**
 * @category env
 */
export declare const isWeixin: boolean;
/**
 * @category env
 */
export declare const isQQ: boolean;
/**
 * @category env
 */
export declare const isQQBrowser: boolean;
/**
 * @category env
 */
export declare const isQzone: boolean;
/**
 * @category env
 */
export declare const isWeibo: boolean;
/**
 * @category env
 */
export declare const isBaidu: boolean;
/**
 * @category file
 */
export declare function getMimeType(path: string): string;
/**
 * @category file
 */
export declare function getExtensionFromMimeType(mimeType: string): string;
/**
 * @category file
 */
export declare function getExtension(path: string): string;
/**
 * Extract numbers from text
 * @param text - Text to be processed
 * @param startsWithZero - Preserve the 0-starting format like `002`
 *
 * @category format
 */
export declare function extractNumber(text: string | number, startsWithZero?: boolean): string;
/**
 * Format amount with two decimal places
 * @param amount - Amount to be processed
 *
 * @category format
 */
export declare function formatAmount(amount: string | number): string;
/**
 * Add ellipses to words that are out of length
 * @param word - The sentence to be processed
 * @param limit - The upper limit
 * @returns The processed word
 *
 * @category format
 */
export declare function ellipsis(word: string, limit: number): string;
/**
 * Capitalize the first letter
 *
 * @category format
 */
export declare function capitalize([first, ...rest]: string): string;
/**
 * @category network
 */
export type ResourcesSupportedWithLoadRes = "js" | "css" | "style";
/**
 * @category network
 */
export interface LoadResOptions {
	type: ResourcesSupportedWithLoadRes;
	id: string;
	resource: string;
}
/**
 * @category network
 */
export declare function loadRes({ type, id, resource }: LoadResOptions): Promise<unknown>;
export type QueryInfo = Record<string, string>;
export type QueryInfoObject = Record<string, string | number | boolean | undefined | null>;
/**
 * Parse URL Query parameters
 * @param url - By default, it is extracted from the browser URL,
 * and this parameter can be parsed from the specified URL
 * @returns Query parameter object,
 * will convert `key1=value1&key2=value2` into an object
 *
 * @category query
 */
export declare function parseQuery(url?: string): QueryInfo;
/**
 * Extract parameter information from URL Query
 * @returns An object containing the request path and parameters object
 *  `path`: Jump path, the same as the routing name in the Web App
 *  `params`: Parameters other than path
 *
 * @category query
 */
export declare function extractQueryInfo(url?: string): {
	path: string;
	params: QueryInfo;
};
/**
 * Get the specified Query parameter
 * @param key - The parameter key name to get
 * @param url - By default, it is extracted from the browser URL,
 * and this parameter can be parsed from the specified URL
 *
 * @category query
 */
export declare function getQuery(key: string, url?: string): string;
/**
 * Serialize Query parameters information
 * @param queryInfoObject - The object of the Query parameter to use for serialization
 *
 * @category query
 */
export declare function stringifyQuery(queryInfoObject: QueryInfoObject): string;
/**
 * Generate random number
 * @param min - The minimum value in the range
 * @param max - The maximum value in the range
 * @param roundingType - Round the result
 *
 * @category random
 */
export declare function randomNumber(min?: number, max?: number, roundingType?: "round" | "ceil" | "floor"): number;
/**
 * Generate random string
 * @param length - The length of the string to be returned
 *
 * @category random
 */
export declare function randomString(length?: number): string;
/**
 * Generate random boolean
 *
 * @category random
 */
export declare function randomBoolean(): boolean;
/**
 * Shuffle the array and sort it randomly
 *
 * @category random
 */
export declare function shuffle(arr: any[]): any[];
/**
 * Verify the mobile phone number format in mainland China
 *
 * @category regexp
 */
export declare function isMob(phoneNumber: number | string): boolean;
/**
 * Verify email format
 *
 * @category regexp
 */
export declare function isEmail(email: string): boolean;
/**
 * Verify url format
 *
 * @category regexp
 */
export declare function isUrl(url: string): boolean;
/**
 * Verify the ID card number format in mainland China
 *
 * @category regexp
 */
export declare function isIdCard(idCardNumber: string): boolean;
/**
 * Verify the bank card number format in mainland China
 *
 * @category regexp
 */
export declare function isBankCard(bankCardNumber: string): boolean;
/**
 * Common runtime environment types
 *
 * @category runtime
 */
export type RuntimeEnv = "development" | "test" | "production" | undefined;
/**
 * Get current runtime environment
 * @precondition The `cross-env` package is installed
 *
 * @category runtime
 */
export declare function getRuntimeEnv(): RuntimeEnv;
/**
 * Current runtime environment
 *
 * @category runtime
 */
export declare const runtimeEnv: RuntimeEnv;
/**
 * Determine whether the specified runtime environment is currently
 * @precondition The `cross-env` package is installed
 *
 * @category runtime
 */
export declare function checkRuntimeEnv(runtimeEnv: string): boolean;
/**
 * Determine whether the current runtime is development
 *
 * @category runtime
 */
export declare const isDevRuntime: boolean;
/**
 * Determine whether the current runtime is test
 *
 * @category runtime
 */
export declare const isTestRuntime: boolean;
/**
 * Determine whether the current runtime is production
 *
 * @category runtime
 */
export declare const isProdRuntime: boolean;
/**
 * @category storage
 */
export type StorageType = "localStorage" | "sessionStorage";
declare class BaseStorage {
	prefix: string;
	private storage;
	constructor(prefix: string, storageType: StorageType);
	/**
	 * Read stored data
	 * @tips The `key` doesn't need to be prefixed
	 * @returns The data in the format before storage
	 */
	get(key: string): any;
	/**
	 * Set storage data
	 */
	set(key: string, value: any): void;
	/**
	 * Remove the specified storage data under the current prefix
	 */
	remove(key: string): void;
	/**
	 * Clear all stored data under the current prefix
	 */
	clear(): void;
	/**
	 * Count the number of storage related to the current prefix
	 */
	count(): number;
	/**
	 * List storage keys associated under the current prefix
	 * @tips All keys without prefix
	 */
	list(): string[];
}
/**
 * localStorage that supports prefixes
 *
 * @category storage
 */
export declare class LocalStorage extends BaseStorage {
	constructor(prefix: string);
}
/**
 * sessionStorage that supports prefixes
 *
 * @category storage
 */
export declare class SessionStorage extends BaseStorage {
	constructor(prefix: string);
}

export {};
