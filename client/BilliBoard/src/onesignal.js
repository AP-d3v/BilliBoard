export async function getPushSubscriptionId() {
  try {
    const OneSignal = await new Promise((resolve) => {
      window.OneSignalDeferred = window.OneSignalDeferred || []
      window.OneSignalDeferred.push((os) => resolve(os))
    })

    await OneSignal.Notifications.requestPermission()
    await OneSignal.User.PushSubscription.optIn()

    for (let i = 0; i < 10; i += 1) {
      const id = OneSignal.User.PushSubscription.id
      if (id) {
        return id
      }
      await new Promise((r) => setTimeout(r, 300))
    }
    return null
  } catch {
    return null
  }
}
