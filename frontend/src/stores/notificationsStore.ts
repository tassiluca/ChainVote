import { defineStore } from 'pinia'
import { ref } from 'vue'
import { apiEndpoints } from '@/commons/globals'
import axios from 'axios'

export interface Notification {
  "date": Date,
  "type": string,
  "body": string,
  "new": boolean
}

export const useNotificationsStore = defineStore('notifications', () => {

  const unreadNotifications = ref(0);

  async function getAllNotifications(): Promise<Notification[]> {
    const response = await axios.get(`${apiEndpoints.API_SERVER}/notifications/all`);
    unreadNotifications.value = response.data.data.filter((n: any) => !n.isRead).length;
    return response.data.data.map((n: any) => ({
      date: new Date(n.date),
      type: n.type,
      body: n.text,
      new: !n.read
    }));
  }

  return { unreadNotifications, getAllNotifications };
});