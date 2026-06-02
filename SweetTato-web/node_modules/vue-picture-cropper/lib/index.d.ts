/* eslint-disable no-undef */
/* eslint-disable no-unused-vars */
/* eslint-disable prettier/prettier */
declare namespace Cropper {
	type Action = "crop" | "move" | "zoom" | "e" | "s" | "w" | "n" | "ne" | "nw" | "se" | "sw" | "all";
	type DragMode = "crop" | "move" | "none";
	type ImageSmoothingQuality = "low" | "medium" | "high";
	type ViewMode = 0 | 1 | 2 | 3;
	interface Data {
		x: number;
		y: number;
		width: number;
		height: number;
		rotate: number;
		scaleX: number;
		scaleY: number;
	}
	interface ContainerData {
		width: number;
		height: number;
	}
	interface ImageData {
		left: number;
		top: number;
		width: number;
		height: number;
		rotate: number;
		scaleX: number;
		scaleY: number;
		naturalWidth: number;
		naturalHeight: number;
		aspectRatio: number;
	}
	interface CanvasData {
		left: number;
		top: number;
		width: number;
		height: number;
		naturalWidth: number;
		naturalHeight: number;
	}
	interface CropBoxData {
		left: number;
		top: number;
		width: number;
		height: number;
	}
	interface GetCroppedCanvasOptions {
		width?: number;
		height?: number;
		minWidth?: number;
		minHeight?: number;
		maxWidth?: number;
		maxHeight?: number;
		rounded?: boolean;
		fillColor?: string;
		imageSmoothingEnabled?: boolean;
		imageSmoothingQuality?: ImageSmoothingQuality;
	}
	interface SetDataOptions {
		x?: number;
		y?: number;
		width?: number;
		height?: number;
		rotate?: number;
		scaleX?: number;
		scaleY?: number;
	}
	interface SetCanvasDataOptions {
		left?: number;
		top?: number;
		width?: number;
		height?: number;
	}
	interface SetCropBoxDataOptions {
		left?: number;
		top?: number;
		width?: number;
		height?: number;
	}
	interface CropperEvent<T extends EventTarget = EventTarget> extends CustomEvent {
		currentTarget: T & {
			cropper: Cropper;
		};
	}
	type ReadyEvent<T extends EventTarget> = CropperEvent<T>;
	interface CropEvent<T extends EventTarget = EventTarget> extends CropperEvent<T> {
		detail: Data;
	}
	interface CropEventData {
		originalEvent: PointerEvent | TouchEvent | MouseEvent;
		action: Action;
	}
	interface CropStartEvent<T extends EventTarget = EventTarget> extends CropperEvent<T> {
		detail: CropEventData;
	}
	interface CropMoveEvent<T extends EventTarget = EventTarget> extends CropperEvent<T> {
		detail: CropEventData;
	}
	interface CropEndEvent<T extends EventTarget = EventTarget> extends CropperEvent<T> {
		detail: CropEventData;
	}
	interface ZoomEventData {
		originalEvent: WheelEvent | PointerEvent | TouchEvent | MouseEvent;
		oldRatio: number;
		ratio: number;
	}
	interface ZoomEvent<T extends EventTarget = EventTarget> extends CropperEvent<T> {
		detail: ZoomEventData;
	}
	interface Options<T extends EventTarget = EventTarget> {
		aspectRatio?: number;
		autoCrop?: boolean;
		autoCropArea?: number;
		background?: boolean;
		center?: boolean;
		checkCrossOrigin?: boolean;
		checkOrientation?: boolean;
		cropBoxMovable?: boolean;
		cropBoxResizable?: boolean;
		data?: SetDataOptions;
		dragMode?: DragMode;
		guides?: boolean;
		highlight?: boolean;
		initialAspectRatio?: number;
		minCanvasHeight?: number;
		minCanvasWidth?: number;
		minContainerHeight?: number;
		minContainerWidth?: number;
		minCropBoxHeight?: number;
		minCropBoxWidth?: number;
		modal?: boolean;
		movable?: boolean;
		preview?: HTMLElement | HTMLElement[] | NodeListOf<HTMLElement> | string;
		responsive?: boolean;
		restore?: boolean;
		rotatable?: boolean;
		scalable?: boolean;
		toggleDragModeOnDblclick?: boolean;
		viewMode?: ViewMode;
		wheelZoomRatio?: number;
		zoomOnTouch?: boolean;
		zoomOnWheel?: boolean;
		zoomable?: boolean;
		ready?(event: ReadyEvent<T>): void;
		crop?(event: CropEvent<T>): void;
		cropend?(event: CropEndEvent<T>): void;
		cropmove?(event: CropMoveEvent<T>): void;
		cropstart?(event: CropStartEvent<T>): void;
		zoom?(event: ZoomEvent<T>): void;
	}
}
declare class Cropper {
	constructor(element: HTMLImageElement, options?: Cropper.Options<HTMLImageElement>);
	constructor(element: HTMLCanvasElement, options?: Cropper.Options<HTMLCanvasElement>);
	clear(): Cropper;
	crop(): Cropper;
	destroy(): Cropper;
	disable(): Cropper;
	enable(): Cropper;
	getCanvasData(): Cropper.CanvasData;
	getContainerData(): Cropper.ContainerData;
	getCropBoxData(): Cropper.CropBoxData;
	getCroppedCanvas(options?: Cropper.GetCroppedCanvasOptions): HTMLCanvasElement;
	getData(rounded?: boolean): Cropper.Data;
	getImageData(): Cropper.ImageData;
	move(offsetX: number, offsetY?: number): Cropper;
	moveTo(x: number, y?: number): Cropper;
	replace(url: string, onlyColorChanged?: boolean): Cropper;
	reset(): Cropper;
	rotate(degree: number): Cropper;
	rotateTo(degree: number): Cropper;
	scale(scaleX: number, scaleY?: number): Cropper;
	scaleX(scaleX: number): Cropper;
	scaleY(scaleY: number): Cropper;
	setAspectRatio(aspectRatio: number): Cropper;
	setCanvasData(data: Cropper.SetCanvasDataOptions): Cropper;
	setCropBoxData(data: Cropper.SetCropBoxDataOptions): Cropper;
	setData(data: Cropper.SetDataOptions): Cropper;
	setDragMode(dragMode: Cropper.DragMode): Cropper;
	zoom(ratio: number): Cropper;
	zoomTo(ratio: number, pivot?: {
		x: number;
		y: number;
	}): Cropper;
	static noConflict(): Cropper;
	static setDefaults(options: Cropper.Options<EventTarget>): void;
}
/**
 * The Cropper instance provided with the component
 */
export interface CropperInstance extends Cropper {
	getDataURL: (options?: Record<string, any>) => string;
	getBlob: (options?: Record<string, any>) => Promise<Blob | null>;
	getFile: (options?: Record<string, any>) => Promise<File | null>;
}
/**
 * Preset Modes Supported by Components
 */
export type SupportedPresetMode = "fixedSize" | "round";
/**
 * Preset options for component props
 * @since 0.4.0
 */
export interface PresetModeOptions {
	mode?: SupportedPresetMode;
	width?: number;
	height?: number;
}
export declare let cropper: CropperInstance | null;
declare const _default: import("vue").DefineComponent<{
	img: {
		type: StringConstructor;
		required: boolean;
		default: string;
	};
	boxStyle: {
		type: ObjectConstructor;
		required: boolean;
		default: () => {};
	};
	options: {
		type: ObjectConstructor;
		required: boolean;
		default: () => {};
	};
	presetMode: {
		type: import("vue").PropType<PresetModeOptions>;
		required: boolean;
		default: () => {};
	};
}, () => JSX.Element, unknown, {}, {}, import("vue").ComponentOptionsMixin, import("vue").ComponentOptionsMixin, {}, string, import("vue").VNodeProps & import("vue").AllowedComponentProps & import("vue").ComponentCustomProps, Readonly<import("vue").ExtractPropTypes<{
	img: {
		type: StringConstructor;
		required: boolean;
		default: string;
	};
	boxStyle: {
		type: ObjectConstructor;
		required: boolean;
		default: () => {};
	};
	options: {
		type: ObjectConstructor;
		required: boolean;
		default: () => {};
	};
	presetMode: {
		type: import("vue").PropType<PresetModeOptions>;
		required: boolean;
		default: () => {};
	};
}>>, {
	img: string;
	boxStyle: Record<string, any>;
	options: Record<string, any>;
	presetMode: PresetModeOptions;
}>;

export {
	_default as default,
};

export {};
