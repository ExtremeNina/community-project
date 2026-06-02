# @withtypes/mime

MIME with types.

Merged [mime](https://www.npmjs.com/package/mime) and [@types/mime](https://www.npmjs.com/package/@types/mime) , install this package to get both, no other features.

## Why?

I got tired of having to install an additional `@types/**` package every time in my TypeScript project to support type inference for these utilities.

So I merged them, but in fact, after installing this package, the original package will still be installed implicitly, and all functions are provided by the original package.

## Installation

Install the package from npm (or yarn, or pnpm).

```bash
# Just install this package
npm i @withtypes/mime
```

This replaces the original problem of needing to install twice:

```bash
# No need to install this now
npm i mime
npm i -D @types/mime
```

## Usage

This package does not have its own function implementation, Just replace the package name in the `import` statement.

```diff
-import mime from 'mime'
+import mime from '@withtypes/mime'
```

So it is exactly the same APIs as the original package in use.

```ts
import mime from '@withtypes/mime'

const type = mime.getType('foo.txt')
console.log(type) // text/plain

const extension = mime.getExtension('text/plain')
console.log(extension) // txt
```

When using the following two import methods in TypeScript, please ensure that the `compilerOptions.moduleResolution` of tsconfig.json is `NodeNext` .

Lite: 

```diff
-import mimelite from 'mime/lite'
+import mimelite from '@withtypes/mime/lite'
```

Mime: 

```diff
-import Mime from 'mime/Mime'
+import Mime from '@withtypes/mime/Mime'
```

## Documentation

See: [Documentation of MIME](https://github.com/broofa/mime#readme)

## License

MIT License © 2022 [chengpeiquan](https://github.com/chengpeiquan)
