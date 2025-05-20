<template>
  <v-alert
    color="info"
    class="mb-4"
    border="start"
    variant="tonal"
  >
    <template v-slot:prepend>
      <v-icon icon="mdi-clock-outline"></v-icon>
    </template>
    <div class="d-flex align-center">
      <div>
        <strong>Next System Update:</strong> {{ schedulerInfo.nextUpdateTime }}
      </div>
      <v-spacer></v-spacer>
      <v-chip
        color="primary"
        size="small"
        class="ml-2"
      >
        {{ timeUntilUpdate }}
      </v-chip>
    </div>
  </v-alert>
</template>

<script>
import axios from 'axios';

export default {
  name: 'SchedulerBanner',
  data() {
    return {
      schedulerInfo: {
        nextUpdateTime: '',
        cronExpression: ''
      },
      timeUntilUpdate: '',
      timer: null
    }
  },
  methods: {
    async fetchSchedulerInfo() {
      try {
        const response = await axios.get('http://localhost:8080/api/v1/scheduler/info');
        console.log('Scheduler info response:', response.data);
        this.schedulerInfo = response.data;
        this.updateTimeUntil();
      } catch (error) {
        console.error('Error fetching scheduler info:', error);
      }
    },
    updateTimeUntil() {
      if (!this.schedulerInfo.nextUpdateTime) return;

      const now = new Date();
      const [date, time] = this.schedulerInfo.nextUpdateTime.split(' ');
      const [day, month, year] = date.split('/');
      const [hours, minutes] = time.split(':');
      
      const nextUpdate = new Date(year, month - 1, day, hours, minutes);
      
      const diff = nextUpdate - now;
      const monthsUntil = Math.floor(diff / (1000 * 60 * 60 * 24 * 30));
      const daysUntil = Math.floor((diff % (1000 * 60 * 60 * 24 * 30)) / (1000 * 60 * 60 * 24));
      const hoursUntil = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
      const minutesUntil = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
      
      let timeString = '';
      if (monthsUntil > 0) {
        timeString += `${monthsUntil}m `;
      }
      if (daysUntil > 0 || monthsUntil > 0) {
        timeString += `${daysUntil}d `;
      }
      if (hoursUntil > 0 || daysUntil > 0 || monthsUntil > 0) {
        timeString += `${hoursUntil}h `;
      }
      timeString += `${minutesUntil}m until next update`;
      
      this.timeUntilUpdate = timeString;
    }
  },
  mounted() {
    this.fetchSchedulerInfo();
    this.timer = setInterval(() => {
      this.updateTimeUntil();
    }, 60000); // Update every minute
  },
  beforeUnmount() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  }
}
</script> 