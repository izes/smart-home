<template>
  <v-app>
    <v-app-bar color="primary" dark>
      <v-app-bar-title>Smart Home Control</v-app-bar-title>
    </v-app-bar>

    <v-main>
      <appliance-list></appliance-list>
    </v-main>
  </v-app>
</template>

<script>
import ApplianceList from './components/ApplianceList.vue'

export default {
  name: 'App',
  components: {
    ApplianceList
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
