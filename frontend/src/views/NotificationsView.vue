<script setup lang="ts">
import { formatTime } from '@/commons/utils'
import { onMounted, watch } from 'vue'
import { useNotificationsStore } from '@/stores/notificationsStore'
import Breadcrumb from '@/components/BreadcrumbComponent.vue'
import PageTitle from '@/components/PageTitleComponent.vue'

const notificationsStore = useNotificationsStore();

onMounted(async () => await viewAllNotifications());

/* When the user is in this view and a notification pops up, the page is updated with the new one. */
watch(() => notificationsStore.unreadNotifications, async (newNotifications) => {
  if (newNotifications > 0) {
    await viewAllNotifications()
  }
});

async function viewAllNotifications() {
  await notificationsStore.getAllNotifications();
  await notificationsStore.readNotifications();
}
</script>

<template>
  <Breadcrumb :paths="[{name: 'User Area', link: '/user'}, {name: 'Notifications', link: '/user/notifications'}]" />
  <div class="container-sm col-md-7 text-center">
    <PageTitle title="Notifications" />
    <div v-if="notificationsStore.notifications.length == 0">
      <p>You don't have notifications, yet.</p>
    </div>
    <div v-for="notification in notificationsStore.notifications" :key="notification.date.toString()" class="row notification">
      <div class="col-1 d-flex flex-column align-items-center justify-content-center">
        <img v-if="notification.new"
             alt="Not read notification, yet."
             src="@/assets/notifications/circle-new-notification.svg"
             width="30" height="30"
        />
      </div>
      <div class="date col-4 d-flex flex-column justify-content-center" :aria-label="notification.date.toString()">
        <span>{{ notification.date.getDate() }}</span>
        <span>{{ notification.date.toLocaleString('it-IT', {month: 'short', year: '2-digit'}).toUpperCase() }}</span>
        <span>{{ formatTime(notification.date) }}</span>
      </div>
      <div class="msg col d-flex flex-column justify-content-center">
        <p>{{ notification.type.toUpperCase().replace('-', ' ') }}</p>
        <p>{{ notification.body }}</p>
      </div>
    </div>
  </div>
</template>

<style scoped>

div.notification {
  margin: 10px 0;
  padding: 10px;
  border-radius: 15px;
  box-shadow: 2px 5px 15px rgba(200, 200, 200, 0.82);
}

div.notification:nth-child(even) {
  background-color: rgba(0, 115, 230, 0.2);
}

div.notification:nth-child(odd) {
  background-color: rgba(217, 217, 217, 0.4);
}

div.date {
  color: #0d6efd;
  span:first-of-type {
    font-weight: bold;
    font-size: 2.5em;
  }
  span:nth-of-type(2) {
    font-weight: bold;
    font-size: 1.1em;
  }
}

div.msg {
  p:first-of-type {
    font-weight: bold;
    font-size: 1.1em;
    color: #e6308a;
    margin: 10px 0;
  }
  p:nth-of-type(2) {
    margin: 0 0 10px 0;
    text-align: left !important;
  }
}
</style>
