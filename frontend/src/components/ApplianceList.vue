<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <scheduler-banner></scheduler-banner>
        <h1 class="text-h4 mb-4">Smart Home Appliances</h1>
        
        <!-- Add New Appliance Button -->
        <v-btn color="primary" class="mb-4" @click="showAddDialog = true">
          <v-icon left>mdi-plus</v-icon>
          Add New Appliance
        </v-btn>

        <!-- Appliance List -->
        <v-row>
          <v-col v-for="appliance in appliances" :key="appliance.id" cols="12" md="6" lg="4">
            <v-card>
              <v-card-title>
                <v-text-field
                  v-model="appliance.name"
                  :label="getApplianceLabel(appliance.type)"
                  @blur="updateAppliance(appliance)"
                  class="mr-4"
                  hide-details
                  dense
                ></v-text-field>
                <v-spacer></v-spacer>
                <!-- Speed selector for Fan -->
                <v-slider
                  v-if="appliance.type === 'fan'"
                  v-model="appliance.speed"
                  :max="2"
                  :min="0"
                  :step="1"
                  :tick-labels="['0', '1', '2']"
                  :color="appliance.speed === 0 ? 'grey' : 'success'"
                  ticks="always"
                  @update:model-value="updateFanSpeed(appliance)"
                  class="mr-2"
                  style="max-width: 150px"
                  hide-details
                ></v-slider>
                <!-- Switch for other appliances -->
                <v-switch
                  v-else
                  v-model="appliance.isOn"
                  :color="appliance.isOn ? 'success' : 'grey'"
                   @update:model-value="toggleAppliance(appliance)"
                ></v-switch>
              </v-card-title>
              
              <v-card-text>
                <div class="text-subtitle-1">Type: {{getApplianceLabel(appliance.type)}}</div>
                <div class="text-subtitle-1">
                  <template v-if="appliance.type === 'fan'">
                    Status: {{ appliance.speed === 0 ? 'Off' : 'On' }}
                    <br>
                    Speed: {{ appliance.speed === 0 ? '0' : appliance.speed === 1 ? '1' : '2' }}
                  </template>
                  <template v-else>
                    Status: {{ appliance.isOn ? 'On' : 'Off' }}
                  </template>
                </div>
                <!-- Additional info for AirConditioner -->
                <div v-if="appliance.type === 'airconditioner'" class="text-subtitle-1">
                  Temperature: {{ appliance.temperature }}°C
                  <br>
                  Mode: {{ appliance.mode }}
                </div>
              </v-card-text>

              <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn
                  icon
                  color="error"
                  @click="confirmDelete(appliance)"
                >
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
              </v-card-actions>
            </v-card>
          </v-col>
        </v-row>
      </v-col>
    </v-row>

    <!-- Add Appliance Dialog -->
    <v-dialog v-model="showAddDialog" max-width="500px">
      <v-card>
        <v-card-title>Add New Appliance</v-card-title>
        <v-card-text>
          <v-form ref="form" v-model="valid">
            <v-text-field
              v-model="newAppliance.name"
              label="Name"
              :rules="[v => !!v || 'Appliance name is required']"
              required
            ></v-text-field>
            <v-select
              v-model="newAppliance.type"
              :items="applianceTypes"
              :item-text="getApplianceLabel"
              item-value="value"
              label="Appliance Type"
              required
            ></v-select>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="error" text @click="showAddDialog = false">Cancel</v-btn>
          <v-btn color="primary" :disabled="!valid" @click="addAppliance">Add</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- Delete Confirmation Dialog -->
    <v-dialog v-model="showDeleteDialog" max-width="400px">
      <v-card>
        <v-card-title>Delete Appliance</v-card-title>
        <v-card-text>
          Are you sure you want to delete {{ applianceToDelete?.name || 'this appliance' }}?
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" text @click="showDeleteDialog = false">Cancel</v-btn>
          <v-btn color="error" text @click="deleteAppliance">Delete</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import axios from 'axios';
import SchedulerBanner from './SchedulerBanner.vue';

// Configure Axios to use the base URL
axios.defaults.baseURL = 'http://localhost:8080';

export default {
  name: 'ApplianceList',
  components: {
    SchedulerBanner
  },
  data() {
    return {
      appliances: [],
      showAddDialog: false,
      showDeleteDialog: false,
      applianceToDelete: null,
      valid: false,
      applianceTypes: [
        { title: 'Light', value: 'light' },
        { title: 'Air Conditioner', value: 'airconditioner' },
        { title: 'Fan', value: 'fan' }
      ],
      newAppliance: {
        name: '',
        type: 'light'
      },
    };
  },
  methods: {
    async fetchAppliances() {
      try {
        const response = await axios.get('/api/v1/appliances');
        this.appliances = response.data;
      } catch (error) {
        console.error('Error fetching appliances:', error);
      }
    },
    async toggleAppliance(appliance) {
      try {
        const payload = {
          type: appliance.type.toLowerCase(),
          name: appliance.name,
          isOn: appliance.isOn
        };

        // Handle each appliance type according to specifications
        if (appliance.type === 'airconditioner') {
          // For AC, mode 'off' means off, otherwise on
          payload.mode = appliance.isOn ? 'cool' : 'off';
          payload.temperature = appliance.temperature || 22; // Use current temperature or default
        }

        await axios.put(`/api/v1/appliances/${appliance.id}`, payload);
        await this.fetchAppliances();
      } catch (error) {
        console.error('Error toggling appliance:', error);
        appliance.isOn = !appliance.isOn; // Revert the switch if the request fails
      }
    },
    async updateAppliance(appliance) {
      try {
        await axios.put(`/api/v1/appliances/${appliance.id}`, appliance);
        await this.fetchAppliances();
      } catch (error) {
        console.error('Error updating appliance:', error);
      }
    },
    async addAppliance() {
      try {
        const { valid } = await this.$refs.form.validate();
        if (!valid) {
          return;
        }

        const payload = {
          name: this.newAppliance.name,
          type: this.newAppliance.type,
          isOn: false // All appliances start turned off
        };
        
        // Add type-specific default values
        if (this.newAppliance.type === 'fan') {
          payload.speed = 0; // Fan starts with speed 0 (off)
        } else if (this.newAppliance.type === 'airconditioner') {
          payload.mode = 'off'; // AC starts in off mode
          payload.temperature = 22; // Default temperature
        }
        
        const response = await axios.post(`/api/v1/appliances`, payload);
        
        if (response.status === 200 || response.status === 201) {
          this.showAddDialog = false;
          await this.fetchAppliances();
          alert(`${this.getApplianceLabel(this.newAppliance.type)} added successfully!`);
          this.resetNewAppliance();
        }
      } catch (error) {
        console.error('Error adding appliance:', error);
        if (error.code === 'ERR_NETWORK') {
          alert('Cannot connect to the server. Please make sure the backend is running.');
        } else if (error.response) {
          console.error('Server response:', error.response.data);
          alert(`Failed to add ${this.getApplianceLabel(this.newAppliance.type)}. Server error: ${error.response.data.message || 'Unknown error'}`);
        } else {
          alert(`Failed to add ${this.getApplianceLabel(this.newAppliance.type)}. Please try again.`);
        }
      }
    },
    resetNewAppliance() {
      this.newAppliance = {
        name: '',
        type: 'light'
      };
    },
    confirmDelete(appliance) {
      this.applianceToDelete = appliance;
      this.showDeleteDialog = true;
    },
    async deleteAppliance() {
      if (!this.applianceToDelete) return;

      try {
        await axios.delete(`/api/v1/appliances/${this.applianceToDelete.id}`);
        await this.fetchAppliances();
        this.showDeleteDialog = false;
        this.applianceToDelete = null;
      } catch (error) {
        console.error('Error deleting appliance:', error);
        alert('Failed to delete appliance. Please try again.');
      }
    },
    async updateFanSpeed(appliance) {
      console.log("Slider changed for fan:", appliance);
      try {
        const payload = {
          type: 'fan',
          name: appliance.name,
          speed: appliance.speed
        };

        const response = await axios.put(`/api/v1/appliances/${appliance.id}`, payload);
        if (response.data) {
          // Update local state with the response data
          Object.assign(appliance, response.data);
        }
      } catch (error) {
        console.error('Error updating fan speed:', error);
        // Revert to previous state on error
        await this.fetchAppliances();
      }
    },
    getApplianceLabel(type) {
    const labels = {
      'FAN': 'Fan',
      'LIGHT': 'Light',
      'AIR_CONDITIONER': 'Air Conditioner',
      'fan': 'Fan',
      'light': 'Light',
      'airconditioner': 'Air Conditioner'
    };
    return labels[type] || type;
    },
    setupSSE() {
      const eventSource = new EventSource('http://localhost:8080/api/v1/events/reset');
      
      eventSource.onmessage = (event) => {
        try {
          const data = JSON.parse(event.data);
          if (data.type === 'RESET') {
            alert('Annual system update: All devices have been turned off for the update.');
            this.fetchAppliances();
          }
        } catch (error) {
          console.error('Error parsing event data:', error);
        }
      };

      eventSource.onerror = (error) => {
        console.error('SSE connection error:', error);
        eventSource.close();
      };
    },
  },
  mounted() {
    this.setupSSE();
  },
  created() {
    this.fetchAppliances();
  }
};
</script>

<style scoped>
.v-card {
  margin-bottom: 16px;
}
</style> 
