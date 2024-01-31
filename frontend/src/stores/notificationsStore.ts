import { defineStore } from 'pinia'
import { type Ref, ref } from 'vue'
import { apiEndpoints } from '@/commons/globals'
import axios from 'axios'

export interface Notification {
  "id": string,
  "date": Date,
  "type": string,
  "body": string,
  "new": boolean
}

export const useNotificationsStore = defineStore('notifications', () => {

  const notifications: Ref<Notification[]> = ref([]);
  const unreadNotifications = ref(0);

  async function getAllNotifications() {
    const response = await axios.get(`${apiEndpoints.API_SERVER}/notifications/all`);
    notifications.value = response.data.data.map((n: any) => ({
      id: n._id,
      date: new Date(n.date),
      type: n.type,
      body: n.text,
      new: !n.isRead
    }));
    unreadNotifications.value = notifications.value!.filter((n: any) => n.new).length;
  }

  async function readNotifications() {
    const newNotifications = notifications.value?.filter((n: Notification) => n.new);
    for (const n1 of newNotifications) {
      await axios.put(`${apiEndpoints.API_SERVER}/notifications/${n1.id}`)
        .catch((error) => console.error(error));
    }
    unreadNotifications.value = 0;
  }

  return { notifications, unreadNotifications, getAllNotifications, readNotifications };
});