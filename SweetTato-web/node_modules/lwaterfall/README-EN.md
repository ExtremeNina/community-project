# lwaterfall-vue3
* 1.Completely under your control, this component only converts your elements to waterfall flow
* 2.Extremely easy to use and suitable for PC/ios/android
* 3.This component is the vue3 version


If you have any questions, please feel free to mention issues and suggestions;. Thank you for your star!
I also hope that you can propose bug or submit your changes to make this component more perfect. Thank you!

## Installation
```
npm  i lwaterfall
``` 
## Introduce
```
 import LWaterfall from 'LWaterfall'
```

## <LWaterfall> Props
Name | Default | type   | desc            | Necessary
-------- |-----|--------|-----------------| ------
cols | 2  | Number | the number of column              | false
colWidth | - | Number | Column width (configuration of this cols will fail) | false


## <LWaterfall> Example
### DOM
```haml
  <l-waterfall v-if="data" :cols="2" >
            <div class="div" style="height:50px" v-for="(item,index) in data" :key="index">
             <!--            <div class="div" style="height:50px" v-for="(item,index) in 10" :key="index">-->
              {{ item }}
            </div>
    </l-waterfall>
    <div @click="b">add data</div>
```
### JavaScript
```js
//Easy to demonstrate and add data
data() {
    return {
      data: [1,2,3,4,5,6,7,8,9,10]
    }
  },
  methods: {
    b() {
      this.data = [...this.data, ...this.data]
    }
  },
```



LinQingYing
