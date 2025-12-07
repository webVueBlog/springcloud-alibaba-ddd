<template>
  <div id="app-wrapper">
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'

onMounted(() => {
  console.log('App component mounted')
  console.log('Router:', import.meta.env)
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
  font-family: 'ZCOOL QingKe HuangYou', 'Ma Shan Zheng', 'Microsoft YaHei', sans-serif;
}

#app {
  width: 100%;
  min-height: 100vh;
  margin: 0;
  padding: 0;
}
</style>

