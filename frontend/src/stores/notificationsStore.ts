import { defineStore } from 'pinia'
import { type Ref, ref } from 'vue'
import { apiEndpoints } from '@/commons/globals'
import axios from 'axios'

export interface Notification {
  "date": Date,
  "type": string,
  "body": string,
  "new": boolean
}

export const useNotificationsStore = defineStore('notifications', () => {

  const notifications: Ref<Notification[] | null> = ref(null);
  const unreadNotifications = ref(0);

  async function getAllNotifications() {
    const response = await axios.get(`${apiEndpoints.API_SERVER}/notifications/all`);
    notifications.value = response.data.data.map((n: any) => ({
      date: new Date(n.date),
      type: n.type,
      body: n.text,
      new: !n.read
    }));
    unreadNotifications.value = notifications.value!.filter((n: any) => n.new).length;
  }

  return { notifications, unreadNotifications, getAllNotifications };
});