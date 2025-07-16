<template>
  <div class="min-h-screen">
    <!-- Mobile Toggle -->
    <div class="md:hidden p-2 bg-white border-b border-pink-200 shadow-md sticky top-0 z-50">
      <button @click="sidebarOpen = !sidebarOpen" class="bg-pink-500 text-white p-2 rounded">
        <i class="fa" :class="sidebarOpen ? 'fa-times' : 'fa-bars'"></i>
      </button>
    </div>

    <!-- Wrapper for sidebar and content -->
    <div class="flex flex-col md:flex-row min-h-screen">
      <!-- Sidebar -->
      <aside
        :class="[
          'bg-white border-r border-pink-200 shadow-lg transition-all duration-300 ease-in-out',
          isDesktop || sidebarOpen ? 'block' : 'hidden',
          'w-full md:w-64 md:block'
        ]"
      >
        <div class="p-6 h-full md:min-h-screen flex flex-col">
          <h2 class="text-2xl font-extrabold text-pink-600 mb-8 tracking-tight">
            <i class="fa fa-user-shield mr-2"></i> Админ Меню
          </h2>
          <nav class="space-y-2 flex-grow">
            <div>
              <button
                @click="goTo('/admin/products'); toggleMenu('products')"
                class="flex items-center justify-between w-full px-4 py-2 text-left text-gray-700 rounded hover:bg-pink-50 hover:text-pink-600 transition"
              >
                <span class="flex items-center"><i class="fa fa-box mr-3 text-pink-500"></i> Продукти</span>
                <i :class="{'fa-chevron-up': activeMenu === 'products', 'fa-chevron-down': activeMenu !== 'products'}" class="fa text-sm"></i>
              </button>
              <transition name="fade">
                <div v-if="activeMenu === 'products'" class="ml-8 mt-1 space-y-1">
                  <button @click="goTo('/admin/products/create')" class="block text-sm text-pink-600 hover:underline">
                    + Добави нов
                  </button>
                </div>
              </transition>
            </div>

            <div>
              <button
                @click="goTo('/admin/categories'); toggleMenu('categories')"
                class="flex items-center justify-between w-full px-4 py-2 text-left text-gray-700 rounded hover:bg-pink-50 hover:text-pink-600 transition"
              >
                <span class="flex items-center"><i class="fa fa-tags mr-3 text-pink-500"></i> Категории</span>
              </button>
            </div>

            <button
              @click="goTo('/admin/orders')"
              class="flex items-center w-full px-4 py-2 text-left text-gray-700 rounded hover:bg-pink-50 hover:text-pink-600 transition"
            >
              <i class="fa fa-shopping-cart mr-3 text-pink-500"></i> Поръчки
            </button>
          </nav>
        </div>
      </aside>

      <!-- Main Content -->
      <main class="flex-1 bg-gray-50 p-6">
        <router-view />
      </main>
    </div>
  </div>
</template>




<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const activeMenu = ref(null);
const sidebarOpen = ref(false);
const isDesktop = ref(false);

const toggleMenu = (menu) => {
  activeMenu.value = activeMenu.value === menu ? null : menu;
};

const goTo = (path) => {
  router.push(path);
  sidebarOpen.value = false;
};

const updateViewport = () => {
  isDesktop.value = window.innerWidth >= 768;
  if (isDesktop.value) sidebarOpen.value = false;
};

onMounted(() => {
  updateViewport();
  window.addEventListener('resize', updateViewport);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', updateViewport);
});
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-5px);
}
</style>
