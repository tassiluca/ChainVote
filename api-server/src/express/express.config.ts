import express, { Application } from "express"

const ExpressConfig = (): Application => {
  const app = express()
  return app
}

export default ExpressConfig